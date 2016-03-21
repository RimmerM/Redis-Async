package com.rimmer.redis.command

import com.rimmer.redis.protocol.writeArray
import com.rimmer.redis.protocol.writeBulkString
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
fun commandHeader(target: ByteBuf, type: ByteArray, argCount: Int): ByteBuf {
    writeArray(target, argCount + 1)
    writeBulkString(target, type)
    return target
}

fun singleArg(command: Command, arg: String, target: ByteBuf) =
    singleArg(command, arg.toByteArray(), target)

fun doubleArg(command: Command, arg0: String, arg1: String, target: ByteBuf) =
    doubleArg(command, arg0.toByteArray(), arg1.toByteArray(), target)

fun tripleArg(command: Command, arg0: String, arg1: String, arg2: String, target: ByteBuf) =
    tripleArg(command, arg0.toByteArray(), arg1.toByteArray(), arg2.toByteArray(), target)

fun singleArg(command: Command, arg: ByteArray, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 1)
    writeBulkString(target, arg)
    return target
}

fun doubleArg(command: Command, arg0: ByteArray, arg1: ByteArray, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 2)
    writeBulkString(target, arg0)
    writeBulkString(target, arg1)
    return target
}

fun tripleArg(command: Command, arg0: ByteArray, arg1: ByteArray, arg2: ByteArray, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 3)
    writeBulkString(target, arg0)
    writeBulkString(target, arg1)
    writeBulkString(target, arg2)
    return target
}

fun multiArg(command: Command, args: Array<out ByteArray>, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 2)
    for(a in args) { writeBulkString(target, a) }
    return target
}

fun multiArg(command: Command, args: Array<out String>, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, args.size)
    for(a in args) { writeBulkString(target, a) }
    return target
}

fun multiArg(command: Command, key: String, args: Array<out String>, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, args.size)
    writeBulkString(target, key)
    for(a in args) { writeBulkString(target, a) }
    return target
}

fun subscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.SUBSCRIBE, channel, target)
fun psubscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.PSUBSCRIBE, channel, target)
fun unsubscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.UNSUBSCRIBE, channel, target)
fun punsubscribe(channel: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.PUNSUBSCRIBE, channel, target)

fun publish(channel: String, message: String, target: ByteBuf = Unpooled.buffer(64)) = doubleArg(Command.PUBLISH, channel, message, target)

fun get(key: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.GET, key, target)

fun set(key: String, value: String, target: ByteBuf = Unpooled.buffer(32)) = set(key, value.toByteArray(), target)
fun set(key: String, value: ByteArray, target: ByteBuf = Unpooled.buffer(32)) = doubleArg(Command.SET, key.toByteArray(), value, target)

fun hget(key: String, field: String, target: ByteBuf = Unpooled.buffer(32)) = doubleArg(Command.HGET, key, field, target)

fun hset(key: String, field: String, value: ByteArray, target: ByteBuf = Unpooled.buffer(32)) =
    tripleArg(Command.HSET, key.toByteArray(), field.toByteArray(), value, target)
fun hset(key: String, field: String, value: String, target: ByteBuf = Unpooled.buffer(32)) = hset(key, field, value.toByteArray(), target)

fun exists(vararg keys: String, target: ByteBuf = Unpooled.buffer(32)) = multiArg(Command.EXISTS, keys, target)
fun del(vararg keys: String, target: ByteBuf = Unpooled.buffer(32)) = multiArg(Command.DEL, keys, target)
fun rpush(key: String, vararg values: String, target: ByteBuf = Unpooled.buffer(32)) = multiArg(Command.RPUSH, key, values, target)
fun sadd(key: String, vararg values: String, target: ByteBuf = Unpooled.buffer(32)) = multiArg(Command.SADD, key, values, target)
fun srem(key: String, vararg values: String, target: ByteBuf = Unpooled.buffer(32)) = multiArg(Command.SREM, key, values, target)
fun zrem(key: String, vararg values: String, target: ByteBuf = Unpooled.buffer(32)) = multiArg(Command.ZREM, key, values, target)

fun zremrange_byrank(key: String, start: Long, stop: Long, target: ByteBuf = Unpooled.buffer(32)) =
    tripleArg(Command.ZREMRANGEBYRANK, key, start.toString(), stop.toString(), target)

fun zadd(key: String, score: String, value: String, target: ByteBuf = Unpooled.buffer(32)) =
    tripleArg(Command.ZADD, key, score, value, target)

fun zrevrange_byscore(key: String, min: String, max: String, target: ByteBuf = Unpooled.buffer(32)) =
    tripleArg(Command.ZREVRANGEBYSCORE, key, max, min, target)
fun zrevrange_byscore(key: String, min: String, max: String, offset: Int, count: Int, target: ByteBuf = Unpooled.buffer(32)) =
    rangeByScore(Command.ZREVRANGEBYSCORE, key, max, min, offset, count, target)

fun zrange_byscore(key: String, min: String, max: String, target: ByteBuf = Unpooled.buffer(32)) =
    tripleArg(Command.ZRANGEBYSCORE, key, min, max, target)
fun zrange_byscore(key: String, min: String, max: String, offset: Int, count: Int, target: ByteBuf = Unpooled.buffer(32)) =
    rangeByScore(Command.ZRANGEBYSCORE, key, min, max, offset, count, target)

fun zrevrange_byscore_withscores(key: String, min: String, max: String, target: ByteBuf = Unpooled.buffer(32)) =
    rangeByScoreWithScores(Command.ZREVRANGEBYSCORE, key, max, min, target)
fun zrevrange_byscore_withscores(key: String, min: String, max: String, offset: Int, count: Int, target: ByteBuf = Unpooled.buffer(32)) =
    rangeByScoreWithScores(Command.ZREVRANGEBYSCORE, key, max, min, offset, count, target)

fun zrange_byscore_withscores(key: String, min: String, max: String, target: ByteBuf = Unpooled.buffer(32)) =
    rangeByScoreWithScores(Command.ZRANGEBYSCORE, key, min, max, target)
fun zrange_byscore_withscores(key: String, min: String, max: String, offset: Int, count: Int, target: ByteBuf = Unpooled.buffer(32)) =
    rangeByScoreWithScores(Command.ZRANGEBYSCORE, key, min, max, offset, count, target)

fun srandmember(key: String, target: ByteBuf = Unpooled.buffer(32)) = singleArg(Command.SRANDMEMBER, key, target)

fun hmget(key: String, vararg fields: String, target: ByteBuf = Unpooled.buffer(64)) =
    multiArg(Command.HMGET, key, fields, target)

fun hmset(key: String, vararg keyValues: Pair<String, String>, target: ByteBuf = Unpooled.buffer(64)): ByteBuf {
    commandHeader(target, Command.HMSET.bytes, 1 + keyValues.size * 2)
    writeBulkString(target, key)
    for(a in keyValues) {
        writeBulkString(target, a.first)
        writeBulkString(target, a.second)
    }
    return target
}

fun multi(target: ByteBuf = Unpooled.buffer(16)) = commandHeader(target, Command.MULTI.bytes, 0)
fun exec(target: ByteBuf = Unpooled.buffer(16)) = commandHeader(target, Command.EXEC.bytes, 0)

private fun rangeByScore(command: Command, key: String, min: String, max: String, offset: Int, count: Int, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 6)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    writeBulkString(target, Keyword.LIMIT.bytes)
    writeBulkString(target, offset.toString())
    writeBulkString(target, count.toString())
    return target
}

private fun rangeByScoreWithScores(command: Command, key: String, min: String, max: String, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 4)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    writeBulkString(target, Keyword.WITHSCORES.bytes)
    return target
}

private fun rangeByScoreWithScores(command: Command, key: String, min: String, max: String, offset: Int, count: Int, target: ByteBuf): ByteBuf {
    commandHeader(target, command.bytes, 7)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    writeBulkString(target, Keyword.WITHSCORES.bytes)
    writeBulkString(target, Keyword.LIMIT.bytes)
    writeBulkString(target, offset.toString())
    writeBulkString(target, count.toString())
    return target
}