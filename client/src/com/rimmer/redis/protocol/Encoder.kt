package com.rimmer.redis.protocol

import io.netty.buffer.ByteBuf

fun writeBulkString(target: ByteBuf, string: String) = writeBulkString(target, string.toByteArray())

fun writeBulkString(target: ByteBuf, bytes: ByteArray) {
    target.writeByte('$'.toInt())
    writeNewlineNumber(target, bytes.size.toLong())
    target.writeBytes(bytes)
    target.writeByte('\r'.toInt())
    target.writeByte('\n'.toInt())
}

fun writeArray(target: ByteBuf, length: Int) {
    target.writeByte('*'.toInt())
    writeNewlineNumber(target, length.toLong())
}

fun writeNumber(target: ByteBuf, value: Long) {
    if(value >= 0 && value < cachedCount) {
        val index = value.toInt()
        val buffer = cachedNumbers[index]
        target.writeBytes(buffer)
    } else if(value == -1L) {
        target.writeBytes(cachedMinusOne)
    } else {
        target.writeBytes(writeNumber(value, false))
    }
}

fun writeNewlineNumber(target: ByteBuf, value: Long) {
    if(value >= 0 && value < cachedCount) {
        val index = value.toInt()
        val buffer = cachedNewlineNumbers[index]
        target.writeBytes(buffer)
    } else if(value == -1L) {
        target.writeBytes(cachedNewlineMinusOne)
    } else {
        target.writeBytes(writeNumber(value, true))
    }
}

private fun writeNumber(value: Long, newline: Boolean): ByteArray {
    val negative = value < 0
    var abs = Math.abs(value)
    var end = (if(value == 0L) 0 else Math.log10(abs.toDouble()).toInt()) + if(negative) 2 else 1

    val bytes = ByteArray(if(newline) end + 2 else end)
    if(newline) {
        bytes[end] = '\r'.toByte()
        bytes[end + 1] = '\n'.toByte()
    }

    if(negative) {
        bytes[0] = '-'.toByte()
    }

    var next = abs
    while(true) {
        next /= 10
        if(next <= 0) break
        end--

        bytes[end] = ('0'.toLong() + abs % 10).toByte()
        abs = next
    }

    end--
    bytes[end] = ('0'.toLong() + abs).toByte()
    return bytes
}

private val cachedCount = 256

private val cachedNumbers = Array(cachedCount) { writeNumber(it.toLong(), false) }
private val cachedNewlineNumbers = Array(cachedCount) { writeNumber(it.toLong(), true) }

private val cachedMinusOne = writeNumber(-1L, false)
private val cachedNewlineMinusOne = writeNumber(-1L, true)