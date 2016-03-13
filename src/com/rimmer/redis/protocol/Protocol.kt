package com.rimmer.redis.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.*

class ArrayBuilder(var length: Int, val target: Array<Response?>)

class ProtocolHandler: ChannelInboundHandlerAdapter(), Connection {
    override val connected: Boolean get() = currentContext != null
    override val idleTime: Long get() = if(responseQueue.isNotEmpty()) 0L else System.nanoTime() - commandEnd

    /** Contains in-flight commands waiting for a response. */
    private val responseQueue: Queue<(Response?, Throwable?) -> Unit> = LinkedList()

    /** The context this handler is currently bound to. */
    private var currentContext: ChannelHandlerContext? = null

    /** Handles reading fragmented array data. */
    private var targetArray: Deque<ArrayBuilder> = ArrayDeque()

    /** Handles reading fragmented string data. */
    private var targetString: ByteBuf? = null
    private var stringBytesLeft = 0

    /** The time the last command returned. */
    private var commandEnd = System.nanoTime()

    override fun command(command: ByteArray, argCount: Int, args: ByteBuf, f: (Response?, Throwable?) -> Unit) {
        responseQueue.offer(f)
        val buffer = currentContext!!.alloc().buffer(128)
        writeArray(buffer, argCount + 1)
        writeBulkString(buffer, command)

        currentContext!!.write(buffer, currentContext!!.voidPromise())
        currentContext!!.writeAndFlush(args, currentContext!!.voidPromise())
    }

    override fun disconnect() {
        val exception = RedisException("Connection is being closed")
        responseQueue.forEach {it(null, exception)}
        currentContext?.close()
    }

    /** Entry point of incoming traffic; handles reading packets and fragmentation. */
    override fun channelRead(context: ChannelHandlerContext, source: Any) {
        currentContext = context
        val packet = source as ByteBuf
        while(packet.readableBytes() > 0) handleValue(packet)
    }

    /** Handles a finished response packet. */
    fun onResponse(response: Response) {
        val array = targetArray.peek()
        if(array == null) {
            val end = System.nanoTime()
            commandEnd = end

            val handler = responseQueue.poll()
            if(handler == null) {
                throw IllegalStateException("Received a response with no associated handler")
            } else {
                handler(response, null)
            }
        } else if(!response.isNull) {
            array.target[array.length] = response
            array.length++

            if(array.length == array.target.size) {
                targetArray.pop()
                onResponse(Response(0, null, null, array.target as Array<Response>, false))
            }
        }
    }

    /** Sends an error to the client. */
    fun onError(error: Throwable) {
        val handler = responseQueue.poll()
        if(handler == null) {
            throw IllegalStateException("Received an error response with no associated handler")
        } else {
            handler(null, error)
        }
    }

    fun handleValue(packet: ByteBuf) {
        if(targetString == null) {
            val type = packet.readByte().toInt()
            when (type) {
                '+'.toInt() -> handleSimpleString(packet)
                '-'.toInt() -> handleError(packet)
                ':'.toInt() -> handleInt(packet)
                '$'.toInt() -> handleBulkString(packet)
                '*'.toInt() -> handleArray(packet)
            }
        } else {
            val readable = Math.min(stringBytesLeft, packet.readableBytes())
            stringBytesLeft -= readable
            if(stringBytesLeft == 0) {
                val buffer = Unpooled.wrappedBuffer(targetString, packet.readSlice(readable))
                onResponse(Response(0, null, buffer, null, false))
            } else {
                targetString = Unpooled.wrappedBuffer(targetString, packet)
            }
        }
    }

    fun handleSimpleString(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        val value = packet.toString(0, length, Charsets.UTF_8)
        packet.skipBytes(2)
        onResponse(Response(0, value, null, null, false))
    }

    fun handleInt(packet: ByteBuf) {
        val int = readInt(packet)
        onResponse(Response(int, null, null, null, false))
    }

    fun handleError(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        val errorText = packet.toString(0, length, Charsets.UTF_8)
        packet.skipBytes(2)
        onError(RedisException(errorText))
    }

    fun handleBulkString(packet: ByteBuf) {
        val length = readInt(packet).toInt()

        // A length of -1 indicates a null value.
        if(length == -1) {
            onResponse(Response(0, null, null, null, true))
            return
        }

        // Read the byte array in chunks if needed.
        if(packet.readableBytes() < length) {
            targetString = packet
            stringBytesLeft = length - packet.readableBytes()
        } else {
            val buffer = packet.readBytes(length)
            packet.skipBytes(2)
            onResponse(Response(0, null, buffer, null, false))
        }
    }

    fun handleArray(packet: ByteBuf) {
        val count = readInt(packet).toInt()
        if(count == -1) {
            // A length of -1 indicates a null value.
            onResponse(Response(0, null, null, null, true))
        } else if(count == 0) {
            onResponse(Response(0, null, null, emptyArray(), false))
        } else {
            targetArray.push(ArrayBuilder(0, arrayOfNulls<Response>(count)))
        }
    }

    fun readInt(packet: ByteBuf): Long {
        var value = 0L
        var sign = 1
        val read = packet.forEachByte {
            if(it == '\r'.toByte()) {
                false
            } else if(it == '-'.toByte()) {
                sign = -1
                true
            } else {
                val v = it - '0'.toByte()
                if(v < 0 || v > 10) throw RedisException("Invalid response: cannot parse '$it' as integer")
                value *= 10
                value += v
                true
            }
        }
        packet.skipBytes(read + 2)
        return value * sign
    }
}
