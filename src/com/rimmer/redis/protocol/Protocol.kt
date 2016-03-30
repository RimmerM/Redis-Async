package com.rimmer.redis.protocol

import com.rimmer.redis.command.*
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder
import java.io.IOException
import java.util.*

class ArrayBuilder(var length: Int, val target: Array<Response?>)

class ProtocolHandler(
    val connectCallback: (Connection?, Throwable?) -> Unit
): ReplayingDecoder<Unit>(), Connection {
    override val connected: Boolean get() = currentContext != null && currentContext!!.channel().isActive
    override val idleTime: Long get() = if(responseQueue.isNotEmpty()) 0L else System.nanoTime() - commandEnd

    /** Contains in-flight commands waiting for a response. */
    private val responseQueue: Queue<(Response?, Throwable?) -> Unit> = LinkedList()

    /** Contains channel listeners if the connection is in channel mode. */
    private val channelListeners = HashMap<Int, (ByteBuf?, Throwable?) -> Unit>()

    /** The context this handler is currently bound to. */
    private var currentContext: ChannelHandlerContext? = null

    /** Handles reading fragmented array data. */
    private var targetArray: Deque<ArrayBuilder> = ArrayDeque()

    /** The time the last command returned. */
    private var commandEnd = System.nanoTime()

    /** Indicates that the connection is in channel mode and cannot send normal commands. */
    private var isChannel = false

    override fun command(command: ByteBuf, f: (Response?, Throwable?) -> Unit) {
        if(isChannel) {
            throw IllegalArgumentException("Cannot execute normal commands while in channel mode.")
        }

        responseQueue.offer(f)
        currentContext!!.writeAndFlush(command, currentContext!!.voidPromise())
    }

    override fun subscribe(channel: String, isPattern: Boolean, f: (ByteBuf?, Throwable?) -> Unit) {
        isChannel = true
        val hash = Arrays.hashCode(channel.toByteArray())
        channelListeners[hash] = f
        val buffer = currentContext!!.alloc().buffer(32)
        val command = if(isPattern) psubscribe(channel, buffer) else subscribe(channel, buffer)
        currentContext!!.writeAndFlush(command)
    }

    override fun unsubscribe(channel: String, isPattern: Boolean) {
        val hash = Arrays.hashCode(channel.toByteArray())
        channelListeners.remove(hash)
        val buffer = currentContext!!.alloc().buffer(32)
        val command = if(isPattern) punsubscribe(channel, buffer) else unsubscribe(channel, buffer)
        currentContext!!.writeAndFlush(command)
    }

    override fun buffer() = currentContext!!.alloc().buffer()

    override fun disconnect() {
        val exception = RedisException("Connection is being closed")
        responseQueue.forEach {it(null, exception)}
        currentContext?.close()
    }

    override fun channelActive(context: ChannelHandlerContext) {
        this.currentContext = context
        connectCallback(this, null)
    }

    /** Entry point of incoming traffic; handles reading packets and sending responses. */
    override fun decode(context: ChannelHandlerContext, packet: ByteBuf, data: MutableList<Any>?) {
        while(packet.readableBytes() > 0) handleValue(packet)
    }

    /** Called when the channel becomes invalid. */
    override fun channelInactive(context: ChannelHandlerContext) {
        super.channelInactive(context)

        // Fail any requests that were still pending.
        val exception = IOException("The redis connection was closed.")
        while(responseQueue.isNotEmpty()) {
            responseQueue.poll().invoke(null, exception)
        }
    }

    /** Handles a finished response packet. */
    fun onResponse(response: Response) {
        checkpoint()

        val array = targetArray.peek()
        if(array == null) {
            val end = System.nanoTime()
            commandEnd = end

            if(isChannel) {
                val message = response.array!!
                val kind = bulkHash(message[0].data!!)
                val channel = bulkHash(message[1].data!!)

                if(kind == messageHash) {
                    channelListeners[channel]?.invoke(message[2].data!!, null)
                }
            } else {
                val handler = responseQueue.poll()
                if (handler == null) {
                    throw IllegalStateException("Received a response with no associated handler")
                } else {
                    handler(response, null)
                }
            }
        } else {
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
        checkpoint()
        val handler = responseQueue.poll()
        if(handler == null) {
            throw IllegalStateException("Received an error response with no associated handler")
        } else {
            handler(null, error)
        }
    }

    fun handleValue(packet: ByteBuf) {
        val type = packet.readByte().toInt()
        when (type) {
            '+'.toInt() -> handleSimpleString(packet)
            '-'.toInt() -> handleError(packet)
            ':'.toInt() -> handleInt(packet)
            '$'.toInt() -> handleBulkString(packet)
            '*'.toInt() -> handleArray(packet)
            else -> throw IllegalArgumentException("Unknown Redis value type '${type.toChar()}'")
        }
    }

    fun handleSimpleString(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        val value = packet.toString(packet.readerIndex(), length, Charsets.UTF_8)
        packet.skipBytes(length + 2)
        onResponse(Response(0, value, null, null, false))
    }

    fun handleInt(packet: ByteBuf) {
        val int = readInt(packet)
        onResponse(Response(int, null, null, null, false))
    }

    fun handleError(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        val errorText = packet.toString(packet.readerIndex(), length, Charsets.UTF_8)
        packet.skipBytes(length + 2)
        onError(RedisException(errorText))
    }

    fun handleBulkString(packet: ByteBuf) {
        val length = readInt(packet).toInt()

        // A length of -1 indicates a null value.
        if(length == -1) {
            onResponse(Response(0, null, null, null, true))
            return
        }

        val buffer = packet.readSlice(length)
        packet.skipBytes(2)
        onResponse(Response(0, null, buffer, null, false))
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
        val start = packet.readerIndex()
        val length = packet.bytesBefore('\r'.toByte())
        packet.forEachByte(start, length) {
            if(it == '-'.toByte()) {
                sign = -1
            } else {
                val v = it - '0'.toByte()
                if(v < 0 || v > 10) throw RedisException("Invalid response: cannot parse '$it' as integer")
                value *= 10
                value += v
            }
            true
        }
        packet.readerIndex(start + length + 2)
        return value * sign
    }

    fun bulkHash(string: ByteBuf): Int {
        val bytes = ByteArray(string.readableBytes())
        string.readBytes(bytes)
        return Arrays.hashCode(bytes)
    }

    companion object {
        val subscribeHash = Arrays.hashCode("subscribe".toByteArray())
        val unsubscribeHash = Arrays.hashCode("unsubscribe".toByteArray())
        val messageHash = Arrays.hashCode("message".toByteArray())
    }
}
