package com.rimmer.redis.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/** Contains pre-converted ByteBuffer values of Redis command names. */
enum class Command {
    PING, SET, GET, QUIT, EXISTS, DEL, TYPE, FLUSHDB, KEYS, RANDOMKEY, RENAME, RENAMENX, RENAMEX, DBSIZE, EXPIRE,
    EXPIREAT, TTL, SELECT, MOVE, FLUSHALL, GETSET, MGET, SETNX, SETEX, MSET, MSETNX, DECRBY, DECR, INCRBY, INCR,
    APPEND, SUBSTR, HSET, HGET, HSETNX, HMSET, HMGET, HINCRBY, HEXISTS, HDEL, HLEN, HKEYS, HVALS, HGETALL, RPUSH,
    LPUSH, LLEN, LRANGE, LTRIM, LINDEX, LSET, LREM, LPOP, RPOP, RPOPLPUSH, SADD, SMEMBERS, SREM, SPOP, SMOVE, SCARD,
    SISMEMBER, SINTER, SINTERSTORE, SUNION, SUNIONSTORE, SDIFF, SDIFFSTORE, SRANDMEMBER, ZADD, ZRANGE, ZREM, ZINCRBY,
    ZRANK, ZREVRANK, ZREVRANGE, ZCARD, ZSCORE, MULTI, DISCARD, EXEC, WATCH, UNWATCH, SORT, BLPOP, BRPOP, AUTH,
    SUBSCRIBE, PUBLISH, UNSUBSCRIBE, PSUBSCRIBE, PUNSUBSCRIBE, PUBSUB, ZCOUNT, ZRANGEBYSCORE, ZREVRANGEBYSCORE,
    ZREMRANGEBYRANK, ZREMRANGEBYSCORE, ZUNIONSTORE, ZINTERSTORE, ZLEXCOUNT, ZRANGEBYLEX, ZREVRANGEBYLEX, ZREMRANGEBYLEX,
    SAVE, BGSAVE, BGREWRITEAOF, LASTSAVE, SHUTDOWN, INFO, MONITOR, SLAVEOF, CONFIG, STRLEN, SYNC, LPUSHX, PERSIST,
    RPUSHX, ECHO, LINSERT, DEBUG, BRPOPLPUSH, SETBIT, GETBIT, BITPOS, SETRANGE, GETRANGE, EVAL, EVALSHA, SCRIPT,
    SLOWLOG, OBJECT, BITCOUNT, BITOP, SENTINEL, DUMP, RESTORE, PEXPIRE, PEXPIREAT, PTTL, INCRBYFLOAT, PSETEX, CLIENT,
    TIME, MIGRATE, HINCRBYFLOAT, SCAN, HSCAN, SSCAN, ZSCAN, WAIT, CLUSTER, ASKING, PFADD, PFCOUNT, PFMERGE, READONLY,
    GEOADD, GEODIST, GEOHASH, GEOPOS, GEORADIUS, GEORADIUSBYMEMBER;

    val bytes: ByteArray
    init { bytes = name.toByteArray() }
}

/** Contains pre-converted ByteBuffer values of Redis keyword names. */
enum class Keyword {
    AGGREGATE, ALPHA, ASC, BY, DESC, GET, LIMIT, MESSAGE, NO, NOSORT, PMESSAGE, PSUBSCRIBE, PUNSUBSCRIBE, OK, ONE,
    QUEUED, SET, STORE, SUBSCRIBE, UNSUBSCRIBE, WEIGHTS, WITHSCORES, RESETSTAT, RESET, FLUSH, EXISTS, LOAD, KILL, LEN,
    REFCOUNT, ENCODING, IDLETIME, AND, OR, XOR, NOT, GETNAME, SETNAME, LIST, MATCH, COUNT;

    val bytes: ByteArray
    init { bytes = name.toLowerCase().toByteArray() }
}

/** Writes the common header of a Redis command. */
fun commandHeader(target: ByteBuf, type: ByteArray, argCount: Int) {
    writeArray(target, argCount + 1)
    writeBulkString(target, type)
}

fun singleArg(command: Command, arg: String, target: ByteBuf = Unpooled.buffer(64)): ByteBuf {
    commandHeader(target, command.bytes, 1)
    writeBulkString(target, arg)
    return target
}

fun doubleArg(command: Command, arg0: String, arg1: String, target: ByteBuf = Unpooled.buffer(64)): ByteBuf {
    commandHeader(target, command.bytes, 2)
    writeBulkString(target, arg0)
    writeBulkString(target, arg1)
    return target
}

fun subscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.SUBSCRIBE, channel, target)
fun psubscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.PSUBSCRIBE, channel, target)
fun unsubscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.UNSUBSCRIBE, channel, target)
fun punsubscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.PUNSUBSCRIBE, channel, target)

fun publish(channel: String, message: String, target: ByteBuf = Unpooled.buffer(64)) = doubleArg(Command.PUBLISH, channel, message, target)