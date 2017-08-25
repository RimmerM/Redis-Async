package com.rimmer.redis.protocol

import com.rimmer.redis.command.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.io.IOException
import java.util.*

private class ArrayBuilder(var length: Int, val target: Array<Response?>)

class ProtocolHandler(
    val connectCallback: (Connection?, Throwable?) -> Unit
): ChannelInboundHandlerAdapter(), Connection {
    enum class ReadState {
        None, SimpleString, Error, Int, BulkString, Array
    }

    override val connected: Boolean get() = currentContext != null && currentContext!!.channel().isActive
    override fun idleTime(nanoTime: Long) = if(responseQueue.isNotEmpty()) 0L else nanoTime - commandAdded
    override fun busyTime(nanoTime: Long) = if(responseQueue.isEmpty()) 0L else nanoTime - commandEnd

    /** Contains in-flight commands waiting for a response. */
    private val responseQueue: Queue<(Response?, Throwable?) -> Unit> = ArrayDeque()

    /** Contains channel listeners if the connection is in channel mode. */
    private val channelListeners = HashMap<Int, (ByteBuf?, Throwable?) -> Unit>()

    /** The context this handler is currently bound to. */
    private var currentContext: ChannelHandlerContext? = null

    /** Handles reading fragmented array data. */
    private var targetArray: Deque<ArrayBuilder> = ArrayDeque()

    /** The time the last command returned. */
    private var commandEnd = System.nanoTime()

    /** The time the last command was added. */
    private var commandAdded = System.nanoTime()

    /** Indicates that the connection is in channel mode and cannot send normal commands. */
    private var isChannel = false

    /** The response type we are currently reading. */
    private var readState = ReadState.None

    /** The length of the string we are currently reading. */
    private var stringLength: Int? = null
    private var arrayLength: Int? = null

    /** The buffer we use to accumulate partial packet data. */
    private var accumulator: ByteBuf? = null

    override fun command(command: ByteBuf, f: (Response?, Throwable?) -> Unit) {
        if(isChannel) {
            throw IllegalArgumentException("Cannot execute normal commands while in channel mode.")
        }

        onPreCommand()
        responseQueue.offer(f)
        currentContext!!.writeAndFlush(command, currentContext!!.voidPromise())
    }

    override fun subscribe(channel: String, isPattern: Boolean, f: (ByteBuf?, Throwable?) -> Unit) {
        isChannel = true
        val hash = Arrays.hashCode(channel.toByteArray())
        channelListeners[hash] = f

        onPreCommand()
        val buffer = currentContext!!.alloc().buffer(32)
        val command = if(isPattern) psubscribe(channel, buffer) else subscribe(channel, buffer)
        currentContext!!.writeAndFlush(command)
    }

    override fun unsubscribe(channel: String, isPattern: Boolean) {
        val hash = Arrays.hashCode(channel.toByteArray())
        channelListeners.remove(hash)

        onPreCommand()
        val buffer = currentContext!!.alloc().buffer(32)
        val command = if(isPattern) punsubscribe(channel, buffer) else unsubscribe(channel, buffer)
        currentContext!!.writeAndFlush(command)
    }

    override fun buffer(): ByteBuf = currentContext!!.alloc().buffer()

    override fun disconnect() {
        val exception = RedisException("Connection is being closed")
        responseQueue.forEach {failCommand(it, exception)}
        responseQueue.clear()
        currentContext?.close()
    }

    override fun channelActive(context: ChannelHandlerContext) {
        this.currentContext = context
        connectCallback(this, null)
    }

    /** Entry point of incoming traffic; handles reading packets and sending responses. */
    override fun channelRead(context: ChannelHandlerContext, message: Any) {
        val source = message as ByteBuf

        // If we had leftover data it is added to the beginning of the packet.
        val packet = if(accumulator == null) {
            source
        } else {
            Unpooled.wrappedBuffer(accumulator, source)
        }

        // Decode packets until the buffer doesn't contain any more full ones.
        while(true) {
            val index = packet.readerIndex()
            handleValue(packet)
            if(index == packet.readerIndex() || packet.readableBytes() == 0) break
        }

        // If there is a partial packet left, we save it until more data is received.
        if(packet.readableBytes() > 0) {
            // This will copy a few bytes, but doing so is better than
            // creating a chain of wrapped buffers holding onto native memory.
            accumulator = Unpooled.buffer(packet.readableBytes())
            accumulator!!.writeBytes(packet)
        } else {
            accumulator = null
        }

        // Deallocate the buffer; for wrapped buffers this releases the contained buffers as well.
        packet.release()
    }

    /** Called when the channel becomes invalid. */
    override fun channelInactive(context: ChannelHandlerContext) {
        // Fail any requests that were still pending.
        val exception = IOException("The redis connection was closed.")
        while(responseQueue.isNotEmpty()) {
            failCommand(responseQueue.poll(), exception)
        }
    }

    /** Updates common state when a command is added. */
    private fun onPreCommand() {
        commandAdded = System.nanoTime()
        if(responseQueue.isEmpty()) commandEnd = commandAdded
    }

    /** Handles a finished response packet. */
    private fun onResponse(response: Response) {
        readState = ReadState.None
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
                if(handler == null) {
                    throw IllegalStateException("Received a response with no associated handler")
                } else {
                    succeedCommand(handler, response)
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
    private fun onError(error: Throwable) {
        readState = ReadState.None
        val handler = responseQueue.poll()
        if(handler == null) {
            throw IllegalStateException("Received an error response with no associated handler")
        } else {
            failCommand(handler, error)
        }
    }

    private fun failCommand(command: (Response?, Throwable?) -> Unit, reason: Throwable) {
        try {
            command(null, reason)
        } catch(e: Throwable) {
            println("Exception in Redis command succeed handler:")
            e.printStackTrace()
        }
    }

    private fun succeedCommand(command: (Response?, Throwable?) -> Unit, response: Response) {
        try {
            command(response, null)
        } catch(e: Throwable) {
            println("Exception in Redis command failure handler:")
            e.printStackTrace()
        }
    }

    private fun handleValue(packet: ByteBuf) {
        when(readState) {
            ReadState.None -> {
                val type = packet.readByte().toInt()
                when (type) {
                    '+'.toInt() -> {
                        readState = ReadState.SimpleString
                        handleSimpleString(packet)
                    }
                    '-'.toInt() -> {
                        readState = ReadState.Error
                        handleError(packet)
                    }
                    ':'.toInt() -> {
                        readState = ReadState.Int
                        handleInt(packet)
                    }
                    '$'.toInt() -> {
                        readState = ReadState.BulkString
                        handleBulkString(packet)
                    }
                    '*'.toInt() -> {
                        readState = ReadState.Array
                        handleArray(packet)
                    }
                    else -> throw IllegalArgumentException("Unknown Redis value type '${type.toChar()}'")
                }
            }
            ReadState.SimpleString -> handleSimpleString(packet)
            ReadState.Error -> handleError(packet)
            ReadState.Int -> handleInt(packet)
            ReadState.BulkString -> handleBulkString(packet)
            ReadState.Array -> handleArray(packet)
        }
    }

    private fun handleSimpleString(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        if(length == -1 || packet.readableBytes() < length + 2) return

        val value = packet.toString(packet.readerIndex(), length, Charsets.UTF_8)
        packet.skipBytes(length + 2)
        onResponse(Response(0, value, null, null, false))
    }

    private fun handleInt(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        if(length == -1 || packet.readableBytes() < length + 2) return

        val int = readInt(packet, length)
        onResponse(Response(int, null, null, null, false))
    }

    private fun handleError(packet: ByteBuf) {
        val length = packet.bytesBefore('\r'.toByte())
        if(length == -1 || packet.readableBytes() < length + 2) return

        val errorText = packet.toString(packet.readerIndex(), length, Charsets.UTF_8)
        packet.skipBytes(length + 2)
        onError(RedisException(errorText))
    }

    private fun handleBulkString(packet: ByteBuf) {
        if(stringLength == null) {
            val intLength = packet.bytesBefore('\r'.toByte())
            if(intLength == -1 || packet.readableBytes() < intLength + 2) return

            val length = readInt(packet, intLength).toInt()
            stringLength = length
        }

        val length = stringLength!!

        // A length of -1 indicates a null value.
        if(length == -1) {
            stringLength = null
            onResponse(Response(0, null, null, null, true))
            return
        }

        if(packet.readableBytes() < length + 2) return

        val bytes = ByteArray(length)
        packet.readBytes(bytes)
        packet.skipBytes(2)

        stringLength = null
        onResponse(Response(0, null, Unpooled.wrappedBuffer(bytes), null, false))
    }

    private fun handleArray(packet: ByteBuf) {
        if(arrayLength == null) {
            val intLength = packet.bytesBefore('\r'.toByte())
            if(intLength == -1 || packet.readableBytes() < intLength + 2) return

            val length = readInt(packet, intLength).toInt()
            arrayLength = length
        }

        val count = arrayLength!!
        arrayLength = null
        readState = ReadState.None
        if(count == -1) {
            // A length of -1 indicates a null value.
            onResponse(Response(0, null, null, null, true))
        } else if(count == 0) {
            onResponse(Response(0, null, null, emptyArray(), false))
        } else {
            targetArray.push(ArrayBuilder(0, arrayOfNulls<Response>(count)))
        }
    }

    private fun readInt(packet: ByteBuf, length: Int): Long {
        var value = 0L
        var sign = 1
        val start = packet.readerIndex()
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
        packet.skipBytes(length + 2)
        return value * sign
    }

    private fun bulkHash(string: ByteBuf): Int {
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
