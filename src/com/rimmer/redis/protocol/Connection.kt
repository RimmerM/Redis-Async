package com.rimmer.redis.protocol

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

/** Represents a single pipelined connection to the database. */
interface Connection {
    /**
     * Executes a Redis command.
     * @param command The command type to execute.
     * @param argCount The number of arguments in args.
     * @param args A serialized RESP array of command arguments.
     */
    fun command(command: ByteArray, argCount: Int, args: ByteBuf, f: (Response?, Throwable?) -> Unit)

    /** Closes this connection. */
    fun disconnect()

    /**
     * Set if the connection is currently connected to a database.
     * This does not guarantee that the connection is still alive.
     */
    val connected: Boolean

    /** The amount of time this connection has been idle since the last action. */
    val idleTime: Long
}

/**
 * Contains the response of a single command.
 * @param int The integer value of the response, if this was an integer response.
 * @param string The string value of the response, if this was a string response.
 * @param data The byte data of the response, if this was a bulk string response.
 * @param array The array value of the response, if this was an array response.
 * @param isNull Set if a null response was received.
 */
class Response(val int: Long, val string: String?, val data: ByteBuf?, val array: Array<Response>?, val isNull: Boolean)

/**
 * Connects to a database.
 * @param group The event loop group to run the client on.
 * @param host The database host to connect to.
 * @param port The port to use for connecting.
 */
fun connect(group: EventLoopGroup, host: String, port: Int, f: (Connection?, Throwable?) -> Unit) {
    val channelType = if(group is EpollEventLoopGroup) EpollSocketChannel::class.java else NioSocketChannel::class.java

    val protocol = ProtocolHandler()

    // Create the connection channel.
    val bootstrap = Bootstrap()
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
        .group(group)
        .channel(channelType)
        .handler(object: ChannelInitializer<Channel>() {
            override fun initChannel(channel: Channel) { channel.pipeline().addLast(protocol) }
        })

    // Try to connect to the database.
    bootstrap.connect(host, port).addListener {
        if(it.isSuccess) {
            f(protocol, null)
        } else {
            f(null, it.cause())
        }
    }
}

class RedisException(cause: String): Exception(cause)
