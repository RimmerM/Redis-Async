package com.rimmer.redis.command

import com.rimmer.redis.protocol.Connection
import com.rimmer.redis.protocol.Response
import com.rimmer.redis.protocol.writeArray
import com.rimmer.redis.protocol.writeBulkString
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBuf

/**
 * Append a value to a key
 * Complexity: O(1). The amortized time complexity is O(1) assuming the appended value is small and the already present value is of any size, since the dynamic string library used by Redis will double the free space available on every reallocation.
 * @return the length of the string after the append operation.
 */
inline fun Connection.append(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_append)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Authenticate to the server
 */
inline fun Connection.auth(password: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_auth)
    writeBulkString(target, password)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Asynchronously rewrite the append-only file
 * @return always `OK`.
 */
inline fun Connection.bgrewriteaof(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_bgrewriteaof)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Asynchronously save the dataset to disk
 */
inline fun Connection.bgsave(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_bgsave)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Count set bits in a string
 * Complexity: O(N)
 * @return The number of bits set to 1.
 */
inline fun Connection.bitcount(key: String, start: Long, end: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_bitcount)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, end.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Perform bitwise operations between strings
 * Complexity: O(N)
 * @return The size of the string stored in the destination key, that is equal to the
size of the longest input string.
 */
inline fun Connection.bitop(operation: String, destkey: String, key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_bitop)
    writeBulkString(target, operation)
    writeBulkString(target, destkey)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Find first bit set or clear in a string
 * Complexity: O(N)
 * @return The command returns the position of the first bit set to 1 or 0 according to the request.
If we look for set bits (the bit argument is 1) and the string is empty or composed of just zero bytes, -1 is returned.
If we look for clear bits (the bit argument is 0) and the string only contains bit set to 1, the function returns the first bit not part of the string on the right. So if the string is three bytes set to the value `0xff` the command `BITPOS key 0` will return 24, since up to bit 23 all the bits are 1.
Basically, the function considers the right of the string as padded with zeros if you look for clear bits and specify no range or the _start_ argument **only**.
However, this behavior changes if you are looking for clear bits and specify a range with both __start__ and __end__. If no clear bit is found in the specified range, the function returns -1 as the user specified a clear range and there are no 0 bits in that range.
 */
inline fun Connection.bitpos(key: String, bit: Long, start: Long, end: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_bitpos)
    writeBulkString(target, key)
    writeBulkString(target, bit.toString())
    writeBulkString(target, start.toString())
    writeBulkString(target, end.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Remove and get the first element in a list, or block until one is available
 * Complexity: O(1)
 * @return specifically:
* A `nil` multi-bulk when no element could be popped and the timeout expired.
* A two-element multi-bulk with the first element being the name of the key
  where an element was popped and the second element being the value of the
  popped element.
 */
inline fun Connection.blpop(key: String, timeout: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_blpop)
    writeBulkString(target, key)
    writeBulkString(target, timeout.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Remove and get the last element in a list, or block until one is available
 * Complexity: O(1)
 * @return specifically:
* A `nil` multi-bulk when no element could be popped and the timeout expired.
* A two-element multi-bulk with the first element being the name of the key
  where an element was popped and the second element being the value of the
  popped element.
 */
inline fun Connection.brpop(key: String, timeout: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_brpop)
    writeBulkString(target, key)
    writeBulkString(target, timeout.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Pop a value from a list, push it to another list and return it; or block until one is available
 * Complexity: O(1)
 * @return the element being popped from `source` and pushed to `destination`.
If `timeout` is reached, a @nil-reply is returned.
## Pattern: Reliable queue
Please see the pattern description in the `RPOPLPUSH` documentation.
## Pattern: Circular list
Please see the pattern description in the `RPOPLPUSH` documentation.
 */
inline fun Connection.brpoplpush(source: String, destination: String, timeout: Long, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_brpoplpush)
    writeBulkString(target, source)
    writeBulkString(target, destination)
    writeBulkString(target, timeout.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill(ipport: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id(ipport: String, id_clientid: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_type(ipport: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_type)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_addr(ipport: String, addr_ipport: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_skipme(ipport: String, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_type(ipport: String, id_clientid: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_type)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_addr(ipport: String, id_clientid: Long, addr_ipport: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_skipme(ipport: String, id_clientid: Long, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_type_addr(ipport: String, addr_ipport: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_type)
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_type_skipme(ipport: String, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_type)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_addr_skipme(ipport: String, addr_ipport: String, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_type_addr(ipport: String, id_clientid: Long, addr_ipport: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_type)
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_type_skipme(ipport: String, id_clientid: Long, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_type)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_addr_skipme(ipport: String, id_clientid: Long, addr_ipport: String, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_type_addr_skipme(ipport: String, addr_ipport: String, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_type)
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Kill the connection of a client
 * Complexity: O(N) where N is the number of client connections
 * @return @simple-string-reply: `OK` if the connection exists and has been closed
When called with the filter / value format:
@integer-reply: the number of clients killed.
 */
inline fun Connection.client_kill_id_type_addr_skipme(ipport: String, id_clientid: Long, addr_ipport: String, skipme_yesno: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_kill)
    writeBulkString(target, ipport)
    writeBulkString(target, kw_id)
    writeBulkString(target, id_clientid.toString())
    writeBulkString(target, kw_type)
    writeBulkString(target, kw_addr)
    writeBulkString(target, addr_ipport)
    writeBulkString(target, kw_skipme)
    writeBulkString(target, skipme_yesno)
    command(target) {r, e -> f(r, e)}
}

/**
 * Get the list of client connections
 * Complexity: O(N) where N is the number of client connections
 * @return a unique string, formatted as follows:
* One client connection per line (separated by LF)
* Each line is composed of a succession of `property=value` fields separated
  by a space character.
Here is the meaning of the fields:
* `id`: an unique 64-bit client ID (introduced in Redis 2.8.12).
* `addr`: address/port of the client
* `fd`: file descriptor corresponding to the socket
* `age`: total duration of the connection in seconds
* `idle`: idle time of the connection in seconds
* `flags`: client flags (see below)
* `db`: current database ID
* `sub`: number of channel subscriptions
* `psub`: number of pattern matching subscriptions
* `multi`: number of commands in a MULTI/EXEC context
* `qbuf`: query buffer length (0 means no query pending)
* `qbuf-free`: free space of the query buffer (0 means the buffer is full)
* `obl`: output buffer length
* `oll`: output list length (replies are queued in this list when the buffer is full)
* `omem`: output buffer memory usage
* `events`: file descriptor events (see below)
* `cmd`: last command played
The client flags can be a combination of:
```
O: the client is a slave in MONITOR mode
S: the client is a normal slave server
M: the client is a master
x: the client is in a MULTI/EXEC context
b: the client is waiting in a blocking operation
i: the client is waiting for a VM I/O (deprecated)
d: a watched keys has been modified - EXEC will fail
c: connection to be closed after writing entire reply
u: the client is unblocked
U: the client is connected via a Unix domain socket
r: the client is in readonly mode against a cluster node
A: connection to be closed ASAP
N: no specific flag set
```
The file descriptor events can be:
```
r: the client socket is readable (event loop)
w: the client socket is writable (event loop)
```
## Notes
New fields are regularly added for debugging purpose. Some could be removed
in the future. A version safe Redis client using this command should parse
the output accordingly (i.e. handling gracefully missing fields, skipping
unknown fields).
 */
inline fun Connection.client_list(crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_list)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Get the current connection name
 * Complexity: O(1)
 * @return The connection name, or a null bulk reply if no name is set.
 */
inline fun Connection.client_getname(crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_getname)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Stop processing commands from clients for some time
 * Complexity: O(1)
 * @return The command returns OK or an error if the timeout is invalid.
 */
inline fun Connection.client_pause(timeout: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_pause)
    writeBulkString(target, timeout.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Instruct the server whether to reply to commands
 * Complexity: O(1)
 * @return @simple-string-reply: `OK`.
 */
inline fun Connection.client_reply(replymode: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_reply)
    writeBulkString(target, replymode)
    command(target) {r, e -> f(r, e)}
}

/**
 * Set the current connection name
 * Complexity: O(1)
 * @return `OK` if the connection name was successfully set.
 */
inline fun Connection.client_setname(connectionname: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_client)
    writeBulkString(target, kw_setname)
    writeBulkString(target, connectionname)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Assign new hash slots to receiving node
 * Complexity: O(N) where N is the total number of hash slot arguments
 * @return `OK` if the command was successful. Otherwise an error is returned.
 */
inline fun Connection.cluster_addslots(slot: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_addslots)
    writeBulkString(target, slot.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Return the number of failure reports active for a given node
 * Complexity: O(N) where N is the number of failure reports
 * @return the number of active failure reports for the node.
 */
inline fun Connection.cluster_count_failure_reports(nodeid: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_count_failure_reports)
    writeBulkString(target, nodeid)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Return the number of local keys in the specified hash slot
 * Complexity: O(1)
 * @return The number of keys in the specified hash slot, or an error if the hash slot is invalid.
 */
inline fun Connection.cluster_countkeysinslot(slot: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_countkeysinslot)
    writeBulkString(target, slot.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set hash slots as unbound in receiving node
 * Complexity: O(N) where N is the total number of hash slot arguments
 * @return `OK` if the command was successful. Otherwise
an error is returned.
 */
inline fun Connection.cluster_delslots(slot: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_delslots)
    writeBulkString(target, slot.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Forces a slave to perform a manual failover of its master.
 * Complexity: O(1)
 * @return `OK` if the command was accepted and a manual failover is going to be attempted. An error if the operation cannot be executed, for example if we are talking with a node which is already a master.
 */
inline fun Connection.cluster_failover(options: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_failover)
    writeBulkString(target, options)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Remove a node from the nodes table
 * Complexity: O(1)
 * @return `OK` if the command was executed successfully, otherwise an error is returned.
 */
inline fun Connection.cluster_forget(nodeid: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_forget)
    writeBulkString(target, nodeid)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Return local key names in the specified hash slot
 * Complexity: O(log(N)) where N is the number of requested keys
 * @return From 0 to *count* key names in a Redis array reply.
 */
inline fun Connection.cluster_getkeysinslot(slot: Long, count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_getkeysinslot)
    writeBulkString(target, slot.toString())
    writeBulkString(target, count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Provides info about Redis Cluster node state
 * Complexity: O(1)
 * @return A map between named fields and values in the form of `<field>:<value>` lines separated by newlines composed by the two bytes `CRLF`.
 */
inline fun Connection.cluster_info(crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_info)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Returns the hash slot of the specified key
 * Complexity: O(N) where N is the number of bytes in the key
 * @return The hash slot number.
 */
inline fun Connection.cluster_keyslot(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_keyslot)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Force a node cluster to handshake with another node
 * Complexity: O(1)
 * @return `OK` if the command was successful. If the address or port specified are invalid an error is returned.
 */
inline fun Connection.cluster_meet(ip: String, port: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_meet)
    writeBulkString(target, ip)
    writeBulkString(target, port.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Get Cluster config for the node
 * Complexity: O(N) where N is the total number of Cluster nodes
 * @return The serialized cluster configuration.
 */
inline fun Connection.cluster_nodes(crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_nodes)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Reconfigure a node as a slave of the specified master node
 * Complexity: O(1)
 * @return `OK` if the command was executed successfully, otherwise an error is returned.
 */
inline fun Connection.cluster_replicate(nodeid: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_replicate)
    writeBulkString(target, nodeid)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Reset a Redis Cluster node
 * Complexity: O(N) where N is the number of known nodes. The command may execute a FLUSHALL as a side effect.
 * @return `OK` if the command was successful. Otherwise an error is returned.
 */
inline fun Connection.cluster_reset(resettype: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_reset)
    writeBulkString(target, resettype)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Forces the node to save cluster state on disk
 * Complexity: O(1)
 * @return `OK` or an error if the operation fails.
 */
inline fun Connection.cluster_saveconfig(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_saveconfig)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the configuration epoch in a new node
 * Complexity: O(1)
 * @return `OK` if the command was executed successfully, otherwise an error is returned.
 */
inline fun Connection.cluster_set_config_epoch(configepoch: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_set_config_epoch)
    writeBulkString(target, configepoch.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Bind a hash slot to a specific node
 * Complexity: O(1)
 * @return All the subcommands return `OK` if the command was successful. Otherwise an error is returned.
## Redis Cluster live resharding explained
The `CLUSTER SETSLOT` command is an important piece used by Redis Cluster in order to migrate all the keys contained in one hash slot from one node to another. This is how the migration is orchestrated, with the help of other commands as well. We'll call the node that has the current ownership of the hash slot the `source` node, and the node where we want to migrate the `destination` node.
1. Set the destination node slot to *importing* state using `CLUSTER SETSLOT <slot> IMPORTING <source-node-id>`.
2. Set the source node slot to *migrating* state using `CLUSTER SETSLOT <slot> MIGRATING <destination-node-id>`.
3. Get keys from the source node with `CLUSTER GETKEYSINSLOT` command and move them into the destination node using the `MIGRATE` command.
4. Use `CLUSTER SETSLOT <slot> NODE <destination-node-id>` in the source or destination.
Notes:
* The order of step 1 and 2 is important. We want the destination node to be ready to accept `ASK` redirections when the source node is configured to redirect.
* Step 4 does not technically need to use `SETSLOT` in the nodes not involved in the resharding, since the configuration will eventually propagate itself, however it is a good idea to do so in order to stop nodes from pointing to the wrong node for the hash slot moved as soon as possible, resulting in less redirections to find the right node.
 */
inline fun Connection.cluster_setslot(slot: Long, subcommand: String, nodeid: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_setslot)
    writeBulkString(target, slot.toString())
    writeBulkString(target, subcommand)
    writeBulkString(target, nodeid)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * List slave nodes of the specified master node
 * Complexity: O(1)
 */
inline fun Connection.cluster_slaves(nodeid: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_slaves)
    writeBulkString(target, nodeid)
    command(target) {r, e -> f(r, e)}
}

/**
 * Get array of Cluster slot to node mappings
 * Complexity: O(N) where N is the total number of Cluster nodes
 * @return nested list of slot ranges with IP/Port mappings.
### Sample Output (old version)
```
127.0.0.1:7001> cluster slots
1) 1) (integer) 0
   2) (integer) 4095
   3) 1) "127.0.0.1"
      2) (integer) 7000
   4) 1) "127.0.0.1"
      2) (integer) 7004
2) 1) (integer) 12288
   2) (integer) 16383
   3) 1) "127.0.0.1"
      2) (integer) 7003
   4) 1) "127.0.0.1"
      2) (integer) 7007
3) 1) (integer) 4096
   2) (integer) 8191
   3) 1) "127.0.0.1"
      2) (integer) 7001
   4) 1) "127.0.0.1"
      2) (integer) 7005
4) 1) (integer) 8192
   2) (integer) 12287
   3) 1) "127.0.0.1"
      2) (integer) 7002
   4) 1) "127.0.0.1"
      2) (integer) 7006
```
### Sample Output (new version, includes IDs)
```
127.0.0.1:30001> cluster slots
1) 1) (integer) 0
   2) (integer) 5460
   3) 1) "127.0.0.1"
      2) (integer) 30001
      3) "09dbe9720cda62f7865eabc5fd8857c5d2678366"
   4) 1) "127.0.0.1"
      2) (integer) 30004
      3) "821d8ca00d7ccf931ed3ffc7e3db0599d2271abf"
2) 1) (integer) 5461
   2) (integer) 10922
   3) 1) "127.0.0.1"
      2) (integer) 30002
      3) "c9d93d9f2c0c524ff34cc11838c2003d8c29e013"
   4) 1) "127.0.0.1"
      2) (integer) 30005
      3) "faadb3eb99009de4ab72ad6b6ed87634c7ee410f"
3) 1) (integer) 10923
   2) (integer) 16383
   3) 1) "127.0.0.1"
      2) (integer) 30003
      3) "044ec91f325b7595e76dbcb18cc688b6a5b434a1"
   4) 1) "127.0.0.1"
      2) (integer) 30006
      3) "58e6e48d41228013e5d9c1c37c5060693925e97e"
```
 */
inline fun Connection.cluster_slots(crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_cluster)
    writeBulkString(target, kw_slots)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get array of Redis command details
 * Complexity: O(N) where N is the total number of Redis commands
 * @return nested list of command details.  Commands are returned
in random order.
 */
inline fun Connection.command(crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_command)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get total number of Redis commands
 * Complexity: O(1)
 * @return number of commands returned by `COMMAND`
 */
inline fun Connection.command_count(crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_command)
    writeBulkString(target, kw_count)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Extract keys given a full Redis command
 * Complexity: O(N) where N is the number of arguments to the command
 * @return list of keys from your command.
 */
inline fun Connection.command_getkeys(crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_command)
    writeBulkString(target, kw_getkeys)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get array of specific Redis command details
 * Complexity: O(N) when N is number of commands to look up
 * @return nested list of command details.
 */
inline fun Connection.command_info(commandname: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_command)
    writeBulkString(target, kw_info)
    writeBulkString(target, commandname)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get the value of a configuration parameter
 */
inline fun Connection.config_get(parameter: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_config)
    writeBulkString(target, kw_get)
    writeBulkString(target, parameter)
    command(target) {r, e -> f(r, e)}
}

/**
 * Rewrite the configuration file with the in memory configuration
 * @return `OK` when the configuration was rewritten properly.
Otherwise an error is returned.
 */
inline fun Connection.config_rewrite(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_config)
    writeBulkString(target, kw_rewrite)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set a configuration parameter to the given value
 * @return `OK` when the configuration was set properly.
Otherwise an error is returned.
 */
inline fun Connection.config_set(parameter: String, value: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_config)
    writeBulkString(target, kw_set)
    writeBulkString(target, parameter)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Reset the stats returned by INFO
 * Complexity: O(1)
 * @return always `OK`.
 */
inline fun Connection.config_resetstat(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_config)
    writeBulkString(target, kw_resetstat)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Return the number of keys in the selected database
 */
inline fun Connection.dbsize(crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_dbsize)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get debugging information about a key
 */
inline fun Connection.debug_object(key: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_debug)
    writeBulkString(target, kw_object)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Make the server crash
 */
inline fun Connection.debug_segfault(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_debug)
    writeBulkString(target, kw_segfault)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Decrement the integer value of a key by one
 * Complexity: O(1)
 * @return the value of `key` after the decrement
 */
inline fun Connection.decr(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_decr)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Decrement the integer value of a key by the given number
 * Complexity: O(1)
 * @return the value of `key` after the decrement
 */
inline fun Connection.decrby(key: String, decrement: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_decrby)
    writeBulkString(target, key)
    writeBulkString(target, decrement.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Delete a key
 * Complexity: O(N) where N is the number of keys that will be removed. When a key to remove holds a value other than a string, the individual complexity for this key is O(M) where M is the number of elements in the list, set, sorted set or hash. Removing a single key that holds a string value is O(1).
 * @return The number of keys that were removed.
 */
inline fun Connection.del(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_del)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Discard all commands issued after MULTI
 * @return always `OK`.
 */
inline fun Connection.discard(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_discard)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Return a serialized version of the value stored at the specified key.
 * Complexity: O(1) to access the key and additional O(N*M) to serialized it, where N is the number of Redis objects composing the value and M their average size. For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).
 * @return the serialized value.
 */
inline fun Connection.dump(key: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_dump)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Echo the given string
 */
inline fun Connection.echo(message: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_echo)
    writeBulkString(target, message)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Execute a Lua script server side
 * Complexity: Depends on the script that is executed.
 */
inline fun Connection.eval(script: String, numkeys: Long, key: String, arg: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_eval)
    writeBulkString(target, script)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, arg)
    command(target) {r, e -> f(r, e)}
}

/**
 * Execute a Lua script server side
 * Complexity: Depends on the script that is executed.
 */
inline fun Connection.evalsha(sha1: String, numkeys: Long, key: String, arg: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_evalsha)
    writeBulkString(target, sha1)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, arg)
    command(target) {r, e -> f(r, e)}
}

/**
 * Execute all commands issued after MULTI
 * @return each element being the reply to each of the commands in the
atomic transaction.
When using `WATCH`, `EXEC` can return a @nil-reply if the execution was aborted.
 */
inline fun Connection.exec(crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_exec)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Determine if a key exists
 * Complexity: O(1)
 * @return specifically:
* `1` if the key exists.
* `0` if the key does not exist.
Since Redis 3.0.3 the command accepts a variable number of keys and the return value is generalized:
* The number of keys existing among the ones specified as arguments. Keys mentioned multiple times and existing are counted multiple times.
 */
inline fun Connection.exists(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_exists)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set a key's time to live in seconds
 * Complexity: O(1)
 * @return specifically:
* `1` if the timeout was set.
* `0` if `key` does not exist or the timeout could not be set.
 */
inline fun Connection.expire(key: String, seconds: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_expire)
    writeBulkString(target, key)
    writeBulkString(target, seconds.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set the expiration for a key as a UNIX timestamp
 * Complexity: O(1)
 * @return specifically:
* `1` if the timeout was set.
* `0` if `key` does not exist or the timeout could not be set (see: `EXPIRE`).
 */
inline fun Connection.expireat(key: String, timestamp: Any, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_expireat)
    writeBulkString(target, key)
    writeBulkString(target, timestamp.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Remove all keys from all databases
 */
inline fun Connection.flushall(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_flushall)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Remove all keys from the current database
 */
inline fun Connection.flushdb(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_flushdb)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Add one or more geospatial items in the geospatial index represented using a sorted set
 * Complexity: O(log(N)) for each item added, where N is the number of elements in the sorted set.
 * @return specifically:
* The number of elements added to the sorted set, not including elements
  already existing for which the score was updated.
 */
inline fun Connection.geoadd(key: String, longitude: Any, latitude: Any, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_geoadd)
    writeBulkString(target, key)
    writeBulkString(target, longitude.toString())
    writeBulkString(target, latitude.toString())
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Returns members of a geospatial index as standard geohash strings
 * Complexity: O(log(N)) for each member requested, where N is the number of elements in the sorted set.
 * @return specifically:
The command returns an array where each element is the Geohash corresponding to
each member name passed as argument to the command.
 */
inline fun Connection.geohash(key: String, member: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_geohash)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Returns longitude and latitude of members of a geospatial index
 * Complexity: O(log(N)) for each member requested, where N is the number of elements in the sorted set.
 * @return specifically:
The command returns an array where each element is a two elements array
representing longitude and latitude (x,y) of each member name passed as
argument to the command.
Non existing elements are reported as NULL elements of the array.
 */
inline fun Connection.geopos(key: String, member: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_geopos)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Returns the distance between two members of a geospatial index
 * Complexity: O(log(N))
 * @return specifically:
The command returns the distance as a double (represented as a string)
in the specified unit, or NULL if one or both the elements are missing.
 */
inline fun Connection.geodist(key: String, member1: String, member2: String, unit: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_geodist)
    writeBulkString(target, key)
    writeBulkString(target, member1)
    writeBulkString(target, member2)
    writeBulkString(target, unit)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a point
 * Complexity: O(N+log(M)) where N is the number of elements inside the bounding box of the circular area delimited by center and radius and M is the number of items inside the index.
 * @return specifically:
* Without any `WITH` option specified, the command just returns a linear array like ["New York","Milan","Paris"].
* If `WITHCOORD`, `WITHDIST` or `WITHHASH` options are specified, the command returns an array of arrays, where each sub-array represents a single item.
When additional information is returned as an array of arrays for each item, the first item in the sub-array is always the name of the returned item. The other information is returned in the following order as successive elements of the sub-array.
1. The distance from the center as a floating point number, in the same unit specified in the radius.
2. The geohash integer.
3. The coordinates as a two items x,y array (longitude,latitude).
So for example the command `GEORADIUS Sicily 15 37 200 km WITHCOORD WITHDIST` will return each item in the following way:
    ["Palermo","190.4424",["13.361389338970184","38.115556395496299"]]
 */
inline fun Connection.georadius(key: String, longitude: Any, latitude: Any, radius: Any, unit: String, withcoord: String, withdist: String, withhash: String, order: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 10)
    writeBulkString(target, kw_georadius)
    writeBulkString(target, key)
    writeBulkString(target, longitude.toString())
    writeBulkString(target, latitude.toString())
    writeBulkString(target, radius.toString())
    writeBulkString(target, unit)
    writeBulkString(target, withcoord)
    writeBulkString(target, withdist)
    writeBulkString(target, withhash)
    writeBulkString(target, order)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a point
 * Complexity: O(N+log(M)) where N is the number of elements inside the bounding box of the circular area delimited by center and radius and M is the number of items inside the index.
 * @return specifically:
* Without any `WITH` option specified, the command just returns a linear array like ["New York","Milan","Paris"].
* If `WITHCOORD`, `WITHDIST` or `WITHHASH` options are specified, the command returns an array of arrays, where each sub-array represents a single item.
When additional information is returned as an array of arrays for each item, the first item in the sub-array is always the name of the returned item. The other information is returned in the following order as successive elements of the sub-array.
1. The distance from the center as a floating point number, in the same unit specified in the radius.
2. The geohash integer.
3. The coordinates as a two items x,y array (longitude,latitude).
So for example the command `GEORADIUS Sicily 15 37 200 km WITHCOORD WITHDIST` will return each item in the following way:
    ["Palermo","190.4424",["13.361389338970184","38.115556395496299"]]
 */
inline fun Connection.georadius_count(key: String, longitude: Any, latitude: Any, radius: Any, unit: String, withcoord: String, withdist: String, withhash: String, order: String, count_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 10)
    writeBulkString(target, kw_georadius)
    writeBulkString(target, key)
    writeBulkString(target, longitude.toString())
    writeBulkString(target, latitude.toString())
    writeBulkString(target, radius.toString())
    writeBulkString(target, unit)
    writeBulkString(target, withcoord)
    writeBulkString(target, withdist)
    writeBulkString(target, withhash)
    writeBulkString(target, order)
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a member
 * Complexity: O(N+log(M)) where N is the number of elements inside the bounding box of the circular area delimited by center and radius and M is the number of items inside the index.
 */
inline fun Connection.georadiusbymember(key: String, member: String, radius: Any, unit: String, withcoord: String, withdist: String, withhash: String, order: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 9)
    writeBulkString(target, kw_georadiusbymember)
    writeBulkString(target, key)
    writeBulkString(target, member)
    writeBulkString(target, radius.toString())
    writeBulkString(target, unit)
    writeBulkString(target, withcoord)
    writeBulkString(target, withdist)
    writeBulkString(target, withhash)
    writeBulkString(target, order)
    command(target) {r, e -> f(r, e)}
}

/**
 * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a member
 * Complexity: O(N+log(M)) where N is the number of elements inside the bounding box of the circular area delimited by center and radius and M is the number of items inside the index.
 */
inline fun Connection.georadiusbymember_count(key: String, member: String, radius: Any, unit: String, withcoord: String, withdist: String, withhash: String, order: String, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 9)
    writeBulkString(target, kw_georadiusbymember)
    writeBulkString(target, key)
    writeBulkString(target, member)
    writeBulkString(target, radius.toString())
    writeBulkString(target, unit)
    writeBulkString(target, withcoord)
    writeBulkString(target, withdist)
    writeBulkString(target, withhash)
    writeBulkString(target, order)
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Get the value of a key
 * Complexity: O(1)
 * @return the value of `key`, or `nil` when `key` does not exist.
 */
inline fun Connection.get(key: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_get)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Returns the bit value at offset in the string value stored at key
 * Complexity: O(1)
 * @return the bit value stored at _offset_.
 */
inline fun Connection.getbit(key: String, offset: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_getbit)
    writeBulkString(target, key)
    writeBulkString(target, offset.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get a substring of the string stored at a key
 * Complexity: O(N) where N is the length of the returned string. The complexity is ultimately determined by the returned length, but because creating a substring from an existing string is very cheap, it can be considered O(1) for small strings.
 */
inline fun Connection.getrange(key: String, start: Long, end: Long, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_getrange)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, end.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Set the string value of a key and return its old value
 * Complexity: O(1)
 * @return the old value stored at `key`, or `nil` when `key` did not exist.
 */
inline fun Connection.getset(key: String, value: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_getset)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Delete one or more hash fields
 * Complexity: O(N) where N is the number of fields to be removed.
 * @return the number of fields that were removed from the hash, not
including specified but non existing fields.
@history
*   `>= 2.4`: Accepts multiple `field` arguments.
    Redis versions older than 2.4 can only remove a field per call.
    To remove multiple fields from a hash in an atomic fashion in earlier
    versions, use a `MULTI` / `EXEC` block.
 */
inline fun Connection.hdel(key: String, field: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hdel)
    writeBulkString(target, key)
    writeBulkString(target, field)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Determine if a hash field exists
 * Complexity: O(1)
 * @return specifically:
* `1` if the hash contains `field`.
* `0` if the hash does not contain `field`, or `key` does not exist.
 */
inline fun Connection.hexists(key: String, field: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hexists)
    writeBulkString(target, key)
    writeBulkString(target, field)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get the value of a hash field
 * Complexity: O(1)
 * @return the value associated with `field`, or `nil` when `field` is not
present in the hash or `key` does not exist.
 */
inline fun Connection.hget(key: String, field: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hget)
    writeBulkString(target, key)
    writeBulkString(target, field)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Get all the fields and values in a hash
 * Complexity: O(N) where N is the size of the hash.
 * @return list of fields and their values stored in the hash, or an
empty list when `key` does not exist.
 */
inline fun Connection.hgetall(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_hgetall)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Increment the integer value of a hash field by the given number
 * Complexity: O(1)
 * @return the value at `field` after the increment operation.
 */
inline fun Connection.hincrby(key: String, field: String, increment: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_hincrby)
    writeBulkString(target, key)
    writeBulkString(target, field)
    writeBulkString(target, increment.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Increment the float value of a hash field by the given amount
 * Complexity: O(1)
 * @return the value of `field` after the increment.
 */
inline fun Connection.hincrbyfloat(key: String, field: String, increment: Any, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_hincrbyfloat)
    writeBulkString(target, key)
    writeBulkString(target, field)
    writeBulkString(target, increment.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Get all the fields in a hash
 * Complexity: O(N) where N is the size of the hash.
 * @return list of fields in the hash, or an empty list when `key` does
not exist.
 */
inline fun Connection.hkeys(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_hkeys)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get the number of fields in a hash
 * Complexity: O(1)
 * @return number of fields in the hash, or `0` when `key` does not exist.
 */
inline fun Connection.hlen(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_hlen)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get the values of all the given hash fields
 * Complexity: O(N) where N is the number of fields being requested.
 * @return list of values associated with the given fields, in the same
order as they are requested.
```cli
HSET myhash field1 "Hello"
HSET myhash field2 "World"
HMGET myhash field1 field2 nofield
```
 */
inline fun Connection.hmget(key: String, field: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hmget)
    writeBulkString(target, key)
    writeBulkString(target, field)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Set multiple hash fields to multiple values
 * Complexity: O(N) where N is the number of fields being set.
 */
inline fun Connection.hmset(key: String, field: String, value: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_hmset)
    writeBulkString(target, key)
    writeBulkString(target, field)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the string value of a hash field
 * Complexity: O(1)
 * @return specifically:
* `1` if `field` is a new field in the hash and `value` was set.
* `0` if `field` already exists in the hash and the value was updated.
 */
inline fun Connection.hset(key: String, field: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_hset)
    writeBulkString(target, key)
    writeBulkString(target, field)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set the value of a hash field, only if the field does not exist
 * Complexity: O(1)
 * @return specifically:
* `1` if `field` is a new field in the hash and `value` was set.
* `0` if `field` already exists in the hash and no operation was performed.
 */
inline fun Connection.hsetnx(key: String, field: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_hsetnx)
    writeBulkString(target, key)
    writeBulkString(target, field)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get the length of the value of a hash field
 * Complexity: O(1)
 * @return the string length of the value associated with `field`, or zero when `field` is not present in the hash or `key` does not exist at all.
 */
inline fun Connection.hstrlen(key: String, field: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hstrlen)
    writeBulkString(target, key)
    writeBulkString(target, field)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get all the values in a hash
 * Complexity: O(N) where N is the size of the hash.
 * @return list of values in the hash, or an empty list when `key` does
not exist.
 */
inline fun Connection.hvals(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_hvals)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Increment the integer value of a key by one
 * Complexity: O(1)
 * @return the value of `key` after the increment
 */
inline fun Connection.incr(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_incr)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Increment the integer value of a key by the given amount
 * Complexity: O(1)
 * @return the value of `key` after the increment
 */
inline fun Connection.incrby(key: String, increment: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_incrby)
    writeBulkString(target, key)
    writeBulkString(target, increment.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Increment the float value of a key by the given amount
 * Complexity: O(1)
 * @return the value of `key` after the increment.
 */
inline fun Connection.incrbyfloat(key: String, increment: Any, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_incrbyfloat)
    writeBulkString(target, key)
    writeBulkString(target, increment.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Get information and statistics about the server
 * @return as a collection of text lines.
Lines can contain a section name (starting with a # character) or a property.
All the properties are in the form of `field:value` terminated by `\r\n`.
```cli
INFO
```
## Notes
Please note depending on the version of Redis some of the fields have been
added or removed. A robust client application should therefore parse the
result of this command by skipping unknown properties, and gracefully handle
missing fields.
Here is the description of fields for Redis >= 2.4.
Here is the meaning of all fields in the **server** section:
*   `redis_version`: Version of the Redis server
*   `redis_git_sha1`:  Git SHA1
*   `redis_git_dirty`: Git dirty flag
*   `os`: Operating system hosting the Redis server
*   `arch_bits`: Architecture (32 or 64 bits)
*   `multiplexing_api`: event loop mechanism used by Redis
*   `gcc_version`: Version of the GCC compiler used to compile the Redis server
*   `process_id`: PID of the server process
*   `run_id`: Random value identifying the Redis server (to be used by Sentinel and Cluster)
*   `tcp_port`: TCP/IP listen port
*   `uptime_in_seconds`: Number of seconds since Redis server start
*   `uptime_in_days`: Same value expressed in days
*   `lru_clock`: Clock incrementing every minute, for LRU management
Here is the meaning of all fields in the **clients** section:
*   `connected_clients`: Number of client connections (excluding connections from slaves)
*   `client_longest_output_list`: longest output list among current client connections
*   `client_biggest_input_buf`: biggest input buffer among current client connections
*   `blocked_clients`: Number of clients pending on a blocking call (BLPOP, BRPOP, BRPOPLPUSH)
Here is the meaning of all fields in the **memory** section:
*   `used_memory`:  total number of bytes allocated by Redis using its
     allocator (either standard **libc**, **jemalloc**, or an alternative allocator such
     as [**tcmalloc**][hcgcpgp]
*   `used_memory_human`: Human readable representation of previous value
*   `used_memory_rss`: Number of bytes that Redis allocated as seen by the
     operating system (a.k.a resident set size). This is the number reported by tools
     such as `top(1)` and `ps(1)`
*   `used_memory_peak`: Peak memory consumed by Redis (in bytes)
*   `used_memory_peak_human`: Human readable representation of previous value
*   `used_memory_lua`: Number of bytes used by the Lua engine
*   `mem_fragmentation_ratio`: Ratio between `used_memory_rss` and `used_memory`
*   `mem_allocator`: Memory allocator, chosen at compile time
Ideally, the `used_memory_rss` value should be only slightly higher than `used_memory`.
When rss >> used, a large difference means there is memory fragmentation
(internal or external), which can be evaluated by checking `mem_fragmentation_ratio`.
When used >> rss, it means part of Redis memory has been swapped off by the operating
system: expect some significant latencies.
Because Redis does not have control over how its allocations are mapped to
memory pages, high `used_memory_rss` is often the result of a spike in memory
usage.
When Redis frees memory, the memory is given back to the allocator, and the
allocator may or may not give the memory back to the system. There may be
a discrepancy between the `used_memory` value and memory consumption as
reported by the operating system. It may be due to the fact memory has been
used and released by Redis, but not given back to the system. The `used_memory_peak`
value is generally useful to check this point.
Here is the meaning of all fields in the **persistence** section:
*   `loading`: Flag indicating if the load of a dump file is on-going
*   `rdb_changes_since_last_save`: Number of changes since the last dump
*   `rdb_bgsave_in_progress`: Flag indicating a RDB save is on-going
*   `rdb_last_save_time`: Epoch-based timestamp of last successful RDB save
*   `rdb_last_bgsave_status`: Status of the last RDB save operation
*   `rdb_last_bgsave_time_sec`: Duration of the last RDB save operation in seconds
*   `rdb_current_bgsave_time_sec`: Duration of the on-going RDB save operation if any
*   `aof_enabled`: Flag indicating AOF logging is activated
*   `aof_rewrite_in_progress`: Flag indicating a AOF rewrite operation is on-going
*   `aof_rewrite_scheduled`: Flag indicating an AOF rewrite operation
     will be scheduled once the on-going RDB save is complete.
*   `aof_last_rewrite_time_sec`: Duration of the last AOF rewrite operation in seconds
*   `aof_current_rewrite_time_sec`: Duration of the on-going AOF rewrite operation if any
*   `aof_last_bgrewrite_status`: Status of the last AOF rewrite operation
`changes_since_last_save` refers to the number of operations that produced
some kind of changes in the dataset since the last time either `SAVE` or
`BGSAVE` was called.
If AOF is activated, these additional fields will be added:
*   `aof_current_size`: AOF current file size
*   `aof_base_size`: AOF file size on latest startup or rewrite
*   `aof_pending_rewrite`: Flag indicating an AOF rewrite operation
     will be scheduled once the on-going RDB save is complete.
*   `aof_buffer_length`: Size of the AOF buffer
*   `aof_rewrite_buffer_length`: Size of the AOF rewrite buffer
*   `aof_pending_bio_fsync`: Number of fsync pending jobs in background I/O queue
*   `aof_delayed_fsync`: Delayed fsync counter
If a load operation is on-going, these additional fields will be added:
*   `loading_start_time`: Epoch-based timestamp of the start of the load operation
*   `loading_total_bytes`: Total file size
*   `loading_loaded_bytes`: Number of bytes already loaded
*   `loading_loaded_perc`: Same value expressed as a percentage
*   `loading_eta_seconds`: ETA in seconds for the load to be complete
Here is the meaning of all fields in the **stats** section:
*   `total_connections_received`: Total number of connections accepted by the server
*   `total_commands_processed`: Total number of commands processed by the server
*   `instantaneous_ops_per_sec`: Number of commands processed per second
*   `rejected_connections`: Number of connections rejected because of `maxclients` limit
*   `expired_keys`: Total number of key expiration events
*   `evicted_keys`: Number of evicted keys due to `maxmemory` limit
*   `keyspace_hits`: Number of successful lookup of keys in the main dictionary
*   `keyspace_misses`: Number of failed lookup of keys in the main dictionary
*   `pubsub_channels`: Global number of pub/sub channels with client subscriptions
*   `pubsub_patterns`: Global number of pub/sub pattern with client subscriptions
*   `latest_fork_usec`: Duration of the latest fork operation in microseconds
Here is the meaning of all fields in the **replication** section:
*   `role`: Value is "master" if the instance is slave of no one, or "slave" if the instance is enslaved to a master.
    Note that a slave can be master of another slave (daisy chaining).
If the instance is a slave, these additional fields are provided:
*   `master_host`: Host or IP address of the master
*   `master_port`: Master listening TCP port
*   `master_link_status`: Status of the link (up/down)
*   `master_last_io_seconds_ago`: Number of seconds since the last interaction with master
*   `master_sync_in_progress`: Indicate the master is syncing to the slave
If a SYNC operation is on-going, these additional fields are provided:
*   `master_sync_left_bytes`: Number of bytes left before syncing is complete
*   `master_sync_last_io_seconds_ago`: Number of seconds since last transfer I/O during a SYNC operation
If the link between master and slave is down, an additional field is provided:
*   `master_link_down_since_seconds`: Number of seconds since the link is down
The following field is always provided:
*   `connected_slaves`: Number of connected slaves
For each slave, the following line is added:
*   `slaveXXX`: id, IP address, port, state
Here is the meaning of all fields in the **cpu** section:
*   `used_cpu_sys`: System CPU consumed by the Redis server
*   `used_cpu_user`:User CPU consumed by the Redis server
*   `used_cpu_sys_children`: System CPU consumed by the background processes
*   `used_cpu_user_children`: User CPU consumed by the background processes
The **commandstats** section provides statistics based on the command type,
including the number of calls, the total CPU time consumed by these commands,
and the average CPU consumed per command execution.
For each command type, the following line is added:
*   `cmdstat_XXX`: `calls=XXX,usec=XXX,usec_per_call=XXX`
The **cluster** section currently only contains a unique field:
*   `cluster_enabled`: Indicate Redis cluster is enabled
The **keyspace** section provides statistics on the main dictionary of each database.
The statistics are the number of keys, and the number of keys with an expiration.
For each database, the following line is added:
*   `dbXXX`: `keys=XXX,expires=XXX`
[hcgcpgp]: http://code.google.com/p/google-perftools/
 */
inline fun Connection.info(section: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_info)
    writeBulkString(target, section)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Find all keys matching the given pattern
 * Complexity: O(N) with N being the number of keys in the database, under the assumption that the key names in the database and the given pattern have limited length.
 * @return list of keys matching `pattern`.
 */
inline fun Connection.keys(pattern: Any, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_keys)
    writeBulkString(target, pattern.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get the UNIX time stamp of the last successful save to disk
 * @return an UNIX time stamp.
 */
inline fun Connection.lastsave(crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_lastsave)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get an element from a list by its index
 * Complexity: O(N) where N is the number of elements to traverse to get to the element at index. This makes asking for the first or the last element of the list O(1).
 * @return the requested element, or `nil` when `index` is out of range.
 */
inline fun Connection.lindex(key: String, index: Long, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_lindex)
    writeBulkString(target, key)
    writeBulkString(target, index.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Insert an element before or after another element in a list
 * Complexity: O(N) where N is the number of elements to traverse before seeing the value pivot. This means that inserting somewhere on the left end on the list (head) can be considered O(1) and inserting somewhere on the right end (tail) is O(N).
 * @return the length of the list after the insert operation, or `-1` when
the value `pivot` was not found.
 */
inline fun Connection.linsert(key: String, where: String, pivot: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_linsert)
    writeBulkString(target, key)
    writeBulkString(target, where)
    writeBulkString(target, pivot)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get the length of a list
 * Complexity: O(1)
 * @return the length of the list at `key`.
 */
inline fun Connection.llen(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_llen)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Remove and get the first element in a list
 * Complexity: O(1)
 * @return the value of the first element, or `nil` when `key` does not exist.
 */
inline fun Connection.lpop(key: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_lpop)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Prepend one or multiple values to a list
 * Complexity: O(1)
 * @return the length of the list after the push operations.
@history
* `>= 2.4`: Accepts multiple `value` arguments.
  In Redis versions older than 2.4 it was possible to push a single value per
  command.
 */
inline fun Connection.lpush(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_lpush)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Prepend a value to a list, only if the list exists
 * Complexity: O(1)
 * @return the length of the list after the push operation.
 */
inline fun Connection.lpushx(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_lpushx)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get a range of elements from a list
 * Complexity: O(S+N) where S is the distance of start offset from HEAD for small lists, from nearest end (HEAD or TAIL) for large lists; and N is the number of elements in the specified range.
 * @return list of elements in the specified range.
 */
inline fun Connection.lrange(key: String, start: Long, stop: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_lrange)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, stop.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Remove elements from a list
 * Complexity: O(N) where N is the length of the list.
 * @return the number of removed elements.
 */
inline fun Connection.lrem(key: String, count: Long, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_lrem)
    writeBulkString(target, key)
    writeBulkString(target, count.toString())
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set the value of an element in a list by its index
 * Complexity: O(N) where N is the length of the list. Setting either the first or the last element of the list is O(1).
 */
inline fun Connection.lset(key: String, index: Long, value: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_lset)
    writeBulkString(target, key)
    writeBulkString(target, index.toString())
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Trim a list to the specified range
 * Complexity: O(N) where N is the number of elements to be removed by the operation.
 */
inline fun Connection.ltrim(key: String, start: Long, stop: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_ltrim)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, stop.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Get the values of all the given keys
 * Complexity: O(N) where N is the number of keys to retrieve.
 * @return list of values at the specified keys.
 */
inline fun Connection.mget(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_mget)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Atomically transfer a key from a Redis instance to another one.
 * Complexity: This command actually executes a DUMP+DEL in the source instance, and a RESTORE in the target instance. See the pages of these commands for time complexity. Also an O(N) data transfer between the two instances is performed.
 * @return The command returns OK on success, or `NOKEY` if no keys were
found in the source instance.
 */
inline fun Connection.migrate(host: String, port: String, key: String, destinationdb: Long, timeout: Long, copy: String, replace: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 8)
    writeBulkString(target, kw_migrate)
    writeBulkString(target, host)
    writeBulkString(target, port)
    writeBulkString(target, key)
    writeBulkString(target, destinationdb.toString())
    writeBulkString(target, timeout.toString())
    writeBulkString(target, copy)
    writeBulkString(target, replace)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Atomically transfer a key from a Redis instance to another one.
 * Complexity: This command actually executes a DUMP+DEL in the source instance, and a RESTORE in the target instance. See the pages of these commands for time complexity. Also an O(N) data transfer between the two instances is performed.
 * @return The command returns OK on success, or `NOKEY` if no keys were
found in the source instance.
 */
inline fun Connection.migrate_keys(host: String, port: String, key: String, destinationdb: Long, timeout: Long, copy: String, replace: String, keys_key: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 8)
    writeBulkString(target, kw_migrate)
    writeBulkString(target, host)
    writeBulkString(target, port)
    writeBulkString(target, key)
    writeBulkString(target, destinationdb.toString())
    writeBulkString(target, timeout.toString())
    writeBulkString(target, copy)
    writeBulkString(target, replace)
    writeBulkString(target, kw_keys)
    writeBulkString(target, keys_key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Listen for all requests received by the server in real time
 * @return flow.
 */
inline fun Connection.monitor(crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_monitor)
    command(target) {r, e -> f(r, e)}
}

/**
 * Move a key to another database
 * Complexity: O(1)
 * @return specifically:
* `1` if `key` was moved.
* `0` if `key` was not moved.
 */
inline fun Connection.move(key: String, db: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_move)
    writeBulkString(target, key)
    writeBulkString(target, db.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set multiple keys to multiple values
 * Complexity: O(N) where N is the number of keys to set.
 * @return always `OK` since `MSET` can't fail.
 */
inline fun Connection.mset(key: String, value: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_mset)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set multiple keys to multiple values, only if none of the keys exist
 * Complexity: O(N) where N is the number of keys to set.
 * @return specifically:
* `1` if the all the keys were set.
* `0` if no key was set (at least one key already existed).
 */
inline fun Connection.msetnx(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_msetnx)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Mark the start of a transaction block
 * @return always `OK`.
 */
inline fun Connection.multi(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_multi)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Inspect the internals of Redis objects
 * Complexity: O(1) for all the currently implemented subcommands.
 * @return * Subcommands `refcount` and `idletime` return integers.
* Subcommand `encoding` returns a bulk reply.
If the object you try to inspect is missing, a null bulk reply is returned.
 */
inline fun Connection.`object`(subcommand: String, arguments: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_object)
    writeBulkString(target, subcommand)
    writeBulkString(target, arguments)
    command(target) {r, e -> f(r, e)}
}

/**
 * Remove the expiration from a key
 * Complexity: O(1)
 * @return specifically:
* `1` if the timeout was removed.
* `0` if `key` does not exist or does not have an associated timeout.
 */
inline fun Connection.persist(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_persist)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set a key's time to live in milliseconds
 * Complexity: O(1)
 * @return specifically:
* `1` if the timeout was set.
* `0` if `key` does not exist or the timeout could not be set.
 */
inline fun Connection.pexpire(key: String, milliseconds: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_pexpire)
    writeBulkString(target, key)
    writeBulkString(target, milliseconds.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set the expiration for a key as a UNIX timestamp specified in milliseconds
 * Complexity: O(1)
 * @return specifically:
* `1` if the timeout was set.
* `0` if `key` does not exist or the timeout could not be set (see: `EXPIRE`).
 */
inline fun Connection.pexpireat(key: String, millisecondstimestamp: Any, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_pexpireat)
    writeBulkString(target, key)
    writeBulkString(target, millisecondstimestamp.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Adds the specified elements to the specified HyperLogLog.
 * Complexity: O(1) to add every element.
 * @return specifically:
* 1 if at least 1 HyperLogLog internal register was altered. 0 otherwise.
 */
inline fun Connection.pfadd(key: String, element: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_pfadd)
    writeBulkString(target, key)
    writeBulkString(target, element)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
 * Complexity: O(1) with every small average constant times when called with a single key. O(N) with N being the number of keys, and much bigger constant times, when called with multiple keys.
 * @return specifically:
* The approximated number of unique elements observed via `PFADD`.
 */
inline fun Connection.pfcount(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_pfcount)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Merge N different HyperLogLogs into a single one.
 * Complexity: O(N) to merge N HyperLogLogs, but with high constant times.
 * @return The command just returns `OK`.
 */
inline fun Connection.pfmerge(destkey: String, sourcekey: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_pfmerge)
    writeBulkString(target, destkey)
    writeBulkString(target, sourcekey)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Ping the server
 */
inline fun Connection.ping(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_ping)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the value and expiration in milliseconds of a key
 * Complexity: O(1)
 */
inline fun Connection.psetex(key: String, milliseconds: Long, value: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_psetex)
    writeBulkString(target, key)
    writeBulkString(target, milliseconds.toString())
    writeBulkString(target, value)
    command(target) {r, e -> f(r, e)}
}

/**
 * Listen for messages published to channels matching the given patterns
 * Complexity: O(N) where N is the number of patterns the client is already subscribed to.
 */
inline fun Connection.psubscribe(pattern: Any, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_psubscribe)
    writeBulkString(target, pattern.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Inspect the state of the Pub/Sub subsystem
 * Complexity: O(N) for the CHANNELS subcommand, where N is the number of active channels, and assuming constant time pattern matching (relatively short channels and patterns). O(N) for the NUMSUB subcommand, where N is the number of requested channels. O(1) for the NUMPAT subcommand.
 * @return a list of active channels, optionally matching the specified pattern.
# `PUBSUB NUMSUB [channel-1 ... channel-N]`
Returns the number of subscribers (not counting clients subscribed to patterns)
for the specified channels.
@return
@array-reply: a list of channels and number of subscribers for every channel. The format is channel, count, channel, count, ..., so the list is flat.
The order in which the channels are listed is the same as the order of the
channels specified in the command call.
Note that it is valid to call this command without channels. In this case it
will just return an empty list.
# `PUBSUB NUMPAT`
Returns the number of subscriptions to patterns (that are performed using the
`PSUBSCRIBE` command). Note that this is not just the count of clients subscribed
to patterns but the total number of patterns all the clients are subscribed to.
@return
@integer-reply: the number of patterns all the clients are subscribed to.
 */
inline fun Connection.pubsub(subcommand: String, argument: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_pubsub)
    writeBulkString(target, subcommand)
    writeBulkString(target, argument)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get the time to live for a key in milliseconds
 * Complexity: O(1)
 * @return TTL in milliseconds, or a negative value in order to signal an error (see the description above).
 */
inline fun Connection.pttl(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_pttl)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Post a message to a channel
 * Complexity: O(N+M) where N is the number of clients subscribed to the receiving channel and M is the total number of subscribed patterns (by any client).
 * @return the number of clients that received the message.
 */
inline fun Connection.publish(channel: String, message: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_publish)
    writeBulkString(target, channel)
    writeBulkString(target, message)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Stop listening for messages posted to channels matching the given patterns
 * Complexity: O(N+M) where N is the number of patterns the client is already subscribed and M is the number of total patterns subscribed in the system (by any client).
 */
inline fun Connection.punsubscribe(pattern: Any, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_punsubscribe)
    writeBulkString(target, pattern.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Close the connection
 * @return always OK.
 */
inline fun Connection.quit(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_quit)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Return a random key from the keyspace
 * Complexity: O(1)
 * @return the random key, or `nil` when the database is empty.
 */
inline fun Connection.randomkey(crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_randomkey)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Enables read queries for a connection to a cluster slave node
 * Complexity: O(1)
 */
inline fun Connection.readonly(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_readonly)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Disables read queries for a connection to a cluster slave node
 * Complexity: O(1)
 */
inline fun Connection.readwrite(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_readwrite)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Rename a key
 * Complexity: O(1)
 */
inline fun Connection.rename(key: String, newkey: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_rename)
    writeBulkString(target, key)
    writeBulkString(target, newkey)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Rename a key, only if the new key does not exist
 * Complexity: O(1)
 * @return specifically:
* `1` if `key` was renamed to `newkey`.
* `0` if `newkey` already exists.
 */
inline fun Connection.renamenx(key: String, newkey: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_renamenx)
    writeBulkString(target, key)
    writeBulkString(target, newkey)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Create a key using the provided serialized value, previously obtained using DUMP.
 * Complexity: O(1) to create the new key and additional O(N*M) to reconstruct the serialized value, where N is the number of Redis objects composing the value and M their average size. For small string values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1). However for sorted set values the complexity is O(N*M*log(N)) because inserting values into sorted sets is O(log(N)).
 * @return The command returns OK on success.
 */
inline fun Connection.restore(key: String, ttl: Long, serializedvalue: String, replace: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_restore)
    writeBulkString(target, key)
    writeBulkString(target, ttl.toString())
    writeBulkString(target, serializedvalue)
    writeBulkString(target, replace)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Return the role of the instance in the context of replication
 * @return where the first element is one of `master`, `slave`, `sentinel` and the additional elements are role-specific as illustrated above.
@history
* This command was introduced in the middle of a Redis stable release, specifically with Redis 2.8.12.
 */
inline fun Connection.role(crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_role)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Remove and get the last element in a list
 * Complexity: O(1)
 * @return the value of the last element, or `nil` when `key` does not exist.
 */
inline fun Connection.rpop(key: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_rpop)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Remove the last element in a list, prepend it to another list and return it
 * Complexity: O(1)
 * @return the element being popped and pushed.
 */
inline fun Connection.rpoplpush(source: String, destination: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_rpoplpush)
    writeBulkString(target, source)
    writeBulkString(target, destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Append one or multiple values to a list
 * Complexity: O(1)
 * @return the length of the list after the push operation.
@history
* `>= 2.4`: Accepts multiple `value` arguments.
  In Redis versions older than 2.4 it was possible to push a single value per
  command.
 */
inline fun Connection.rpush(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_rpush)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Append a value to a list, only if the list exists
 * Complexity: O(1)
 * @return the length of the list after the push operation.
 */
inline fun Connection.rpushx(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_rpushx)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Add one or more members to a set
 * Complexity: O(N) where N is the number of members to be added.
 * @return the number of elements that were added to the set, not including
all the elements already present into the set.
@history
* `>= 2.4`: Accepts multiple `member` arguments.
  Redis versions before 2.4 are only able to add a single member per call.
 */
inline fun Connection.sadd(key: String, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sadd)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Synchronously save the dataset to disk
 * @return The commands returns OK on success.
 */
inline fun Connection.save(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_save)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Get the number of members in a set
 * Complexity: O(1)
 * @return the cardinality (number of elements) of the set, or `0` if `key`
does not exist.
 */
inline fun Connection.scard(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_scard)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set the debug mode for executed scripts.
 * Complexity: O(1)
 * @return `OK`.
 */
inline fun Connection.script_debug(mode: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_script)
    writeBulkString(target, kw_debug)
    writeBulkString(target, mode)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Check existence of scripts in the script cache.
 * Complexity: O(N) with N being the number of scripts to check (so checking a single script is an O(1) operation).
 * @return he command returns an array of integers that correspond to
the specified SHA1 digest arguments.
For every corresponding SHA1 digest of a script that actually exists in the
script cache, an 1 is returned, otherwise 0 is returned.
 */
inline fun Connection.script_exists(script: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_script)
    writeBulkString(target, kw_exists)
    writeBulkString(target, script)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Remove all the scripts from the script cache.
 * Complexity: O(N) with N being the number of scripts in cache
 */
inline fun Connection.script_flush(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_script)
    writeBulkString(target, kw_flush)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Kill the script currently in execution.
 * Complexity: O(1)
 */
inline fun Connection.script_kill(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_script)
    writeBulkString(target, kw_kill)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Load the specified Lua script into the script cache.
 * Complexity: O(N) with N being the length in bytes of the script body.
 * @return his command returns the SHA1 digest of the script added into the
script cache.
 */
inline fun Connection.script_load(script: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_script)
    writeBulkString(target, kw_load)
    writeBulkString(target, script)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Subtract multiple sets
 * Complexity: O(N) where N is the total number of elements in all given sets.
 * @return list with members of the resulting set.
 */
inline fun Connection.sdiff(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_sdiff)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Subtract multiple sets and store the resulting set in a key
 * Complexity: O(N) where N is the total number of elements in all given sets.
 * @return the number of elements in the resulting set.
 */
inline fun Connection.sdiffstore(destination: String, key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sdiffstore)
    writeBulkString(target, destination)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Change the selected database for the current connection
 */
inline fun Connection.select(index: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_select)
    writeBulkString(target, index.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the string value of a key
 * Complexity: O(1)
 * @return `OK` if `SET` was executed correctly.
@nil-reply: a Null Bulk Reply is returned if the `SET` operation was not performed because the user specified the `NX` or `XX` option but the condition was not met.
 */
inline fun Connection.set(key: String, value: String, condition: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_set)
    writeBulkString(target, key)
    writeBulkString(target, value)
    writeBulkString(target, condition)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the string value of a key
 * Complexity: O(1)
 * @return `OK` if `SET` was executed correctly.
@nil-reply: a Null Bulk Reply is returned if the `SET` operation was not performed because the user specified the `NX` or `XX` option but the condition was not met.
 */
inline fun Connection.set_ex(key: String, value: String, condition: String, ex_seconds: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_set)
    writeBulkString(target, key)
    writeBulkString(target, value)
    writeBulkString(target, condition)
    writeBulkString(target, kw_ex)
    writeBulkString(target, ex_seconds.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the string value of a key
 * Complexity: O(1)
 * @return `OK` if `SET` was executed correctly.
@nil-reply: a Null Bulk Reply is returned if the `SET` operation was not performed because the user specified the `NX` or `XX` option but the condition was not met.
 */
inline fun Connection.set_px(key: String, value: String, condition: String, px_milliseconds: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_set)
    writeBulkString(target, key)
    writeBulkString(target, value)
    writeBulkString(target, condition)
    writeBulkString(target, kw_px)
    writeBulkString(target, px_milliseconds.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the string value of a key
 * Complexity: O(1)
 * @return `OK` if `SET` was executed correctly.
@nil-reply: a Null Bulk Reply is returned if the `SET` operation was not performed because the user specified the `NX` or `XX` option but the condition was not met.
 */
inline fun Connection.set_ex_px(key: String, value: String, condition: String, ex_seconds: Long, px_milliseconds: Long, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_set)
    writeBulkString(target, key)
    writeBulkString(target, value)
    writeBulkString(target, condition)
    writeBulkString(target, kw_ex)
    writeBulkString(target, ex_seconds.toString())
    writeBulkString(target, kw_px)
    writeBulkString(target, px_milliseconds.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Sets or clears the bit at offset in the string value stored at key
 * Complexity: O(1)
 * @return the original bit value stored at _offset_.
 */
inline fun Connection.setbit(key: String, offset: Long, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_setbit)
    writeBulkString(target, key)
    writeBulkString(target, offset.toString())
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Set the value and expiration of a key
 * Complexity: O(1)
 */
inline fun Connection.setex(key: String, seconds: Long, value: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_setex)
    writeBulkString(target, key)
    writeBulkString(target, seconds.toString())
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Set the value of a key, only if the key does not exist
 * Complexity: O(1)
 * @return specifically:
* `1` if the key was set
* `0` if the key was not set
 */
inline fun Connection.setnx(key: String, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_setnx)
    writeBulkString(target, key)
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Overwrite part of a string at key starting at the specified offset
 * Complexity: O(1), not counting the time taken to copy the new string in place. Usually, this string is very small so the amortized complexity is O(1). Otherwise, complexity is O(M) with M being the length of the value argument.
 * @return the length of the string after it was modified by the command.
 */
inline fun Connection.setrange(key: String, offset: Long, value: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_setrange)
    writeBulkString(target, key)
    writeBulkString(target, offset.toString())
    writeBulkString(target, value)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Synchronously save the dataset to disk and then shut down the server
 * @return n error.
On success nothing is returned since the server quits and the connection is
closed.
 */
inline fun Connection.shutdown(savemode: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_shutdown)
    writeBulkString(target, savemode)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Intersect multiple sets
 * Complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.
 * @return list with members of the resulting set.
 */
inline fun Connection.sinter(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_sinter)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Intersect multiple sets and store the resulting set in a key
 * Complexity: O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.
 * @return the number of elements in the resulting set.
 */
inline fun Connection.sinterstore(destination: String, key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sinterstore)
    writeBulkString(target, destination)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Determine if a given value is a member of a set
 * Complexity: O(1)
 * @return specifically:
* `1` if the element is a member of the set.
* `0` if the element is not a member of the set, or if `key` does not exist.
 */
inline fun Connection.sismember(key: String, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sismember)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Make the server a slave of another instance, or promote it as master
 */
inline fun Connection.slaveof(host: String, port: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_slaveof)
    writeBulkString(target, host)
    writeBulkString(target, port)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Manages the Redis slow queries log
 */
inline fun Connection.slowlog(subcommand: String, argument: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_slowlog)
    writeBulkString(target, subcommand)
    writeBulkString(target, argument)
    command(target) {r, e -> f(r, e)}
}

/**
 * Get all the members in a set
 * Complexity: O(N) where N is the set cardinality.
 * @return all elements of the set.
 */
inline fun Connection.smembers(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_smembers)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Move a member from one set to another
 * Complexity: O(1)
 * @return specifically:
* `1` if the element is moved.
* `0` if the element is not a member of `source` and no operation was performed.
 */
inline fun Connection.smove(source: String, destination: String, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_smove)
    writeBulkString(target, source)
    writeBulkString(target, destination)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort(key: String, order: String, sorting: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by(key: String, order: String, sorting: String, by_pattern: Any, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_limit(key: String, order: String, sorting: String, limit_offset: Long, limit_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_get(key: String, order: String, sorting: String, get_pattern: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_store(key: String, order: String, sorting: String, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_limit(key: String, order: String, sorting: String, by_pattern: Any, limit_offset: Long, limit_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_get(key: String, order: String, sorting: String, by_pattern: Any, get_pattern: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_store(key: String, order: String, sorting: String, by_pattern: Any, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_limit_get(key: String, order: String, sorting: String, limit_offset: Long, limit_count: Long, get_pattern: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_limit_store(key: String, order: String, sorting: String, limit_offset: Long, limit_count: Long, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_get_store(key: String, order: String, sorting: String, get_pattern: String, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_limit_get(key: String, order: String, sorting: String, by_pattern: Any, limit_offset: Long, limit_count: Long, get_pattern: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_limit_store(key: String, order: String, sorting: String, by_pattern: Any, limit_offset: Long, limit_count: Long, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_get_store(key: String, order: String, sorting: String, by_pattern: Any, get_pattern: String, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_limit_get_store(key: String, order: String, sorting: String, limit_offset: Long, limit_count: Long, get_pattern: String, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Sort the elements in a list, set or sorted set
 * Complexity: O(N+M*log(M)) where N is the number of elements in the list or set to sort, and M the number of returned elements. When the elements are not sorted, complexity is currently O(N) as there is a copy step that will be avoided in next releases.
 * @return list of sorted elements.
 */
inline fun Connection.sort_by_limit_get_store(key: String, order: String, sorting: String, by_pattern: Any, limit_offset: Long, limit_count: Long, get_pattern: String, store_destination: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_sort)
    writeBulkString(target, key)
    writeBulkString(target, order)
    writeBulkString(target, sorting)
    writeBulkString(target, kw_by)
    writeBulkString(target, by_pattern.toString())
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    writeBulkString(target, kw_get)
    writeBulkString(target, get_pattern)
    writeBulkString(target, kw_store)
    writeBulkString(target, store_destination)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Remove and return one or multiple random members from a set
 * Complexity: O(1)
 * @return the removed element, or `nil` when `key` does not exist.
 */
inline fun Connection.spop(key: String, count: Long, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_spop)
    writeBulkString(target, key)
    writeBulkString(target, count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Get one or multiple random members from a set
 * Complexity: Without the count argument O(1), otherwise O(N) where N is the absolute value of the passed count.
 * @return without the additional `count` argument the command returns a Bulk Reply with the randomly selected element, or `nil` when `key` does not exist.
@array-reply: when the additional `count` argument is passed the command returns an array of elements, or an empty array when `key` does not exist.
 */
inline fun Connection.srandmember(key: String, count: Long, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_srandmember)
    writeBulkString(target, key)
    writeBulkString(target, count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Remove one or more members from a set
 * Complexity: O(N) where N is the number of members to be removed.
 * @return the number of members that were removed from the set, not
including non existing members.
@history
* `>= 2.4`: Accepts multiple `member` arguments.
  Redis versions older than 2.4 can only remove a set member per call.
 */
inline fun Connection.srem(key: String, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_srem)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get the length of the value stored in a key
 * Complexity: O(1)
 * @return the length of the string at `key`, or `0` when `key` does not
exist.
 */
inline fun Connection.strlen(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_strlen)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Listen for messages published to the given channels
 * Complexity: O(N) where N is the number of channels to subscribe to.
 */
inline fun Connection.subscribe(channel: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_subscribe)
    writeBulkString(target, channel)
    command(target) {r, e -> f(r, e)}
}

/**
 * Add multiple sets
 * Complexity: O(N) where N is the total number of elements in all given sets.
 * @return list with members of the resulting set.
 */
inline fun Connection.sunion(key: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_sunion)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Add multiple sets and store the resulting set in a key
 * Complexity: O(N) where N is the total number of elements in all given sets.
 * @return the number of elements in the resulting set.
 */
inline fun Connection.sunionstore(destination: String, key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sunionstore)
    writeBulkString(target, destination)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Internal command used for replication
 */
inline fun Connection.sync(crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_sync)
    command(target) {r, e -> f(r, e)}
}

/**
 * Return the current server time
 * Complexity: O(1)
 * @return specifically:
A multi bulk reply containing two elements:
* unix time in seconds.
* microseconds.
 */
inline fun Connection.time(crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_time)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Get the time to live for a key
 * Complexity: O(1)
 * @return TTL in seconds, or a negative value in order to signal an error (see the description above).
 */
inline fun Connection.ttl(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_ttl)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Determine the type stored at key
 * Complexity: O(1)
 * @return type of `key`, or `none` when `key` does not exist.
 */
inline fun Connection.type(key: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_type)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Stop listening for messages posted to the given channels
 * Complexity: O(N) where N is the number of clients already subscribed to a channel.
 */
inline fun Connection.unsubscribe(channel: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_unsubscribe)
    writeBulkString(target, channel)
    command(target) {r, e -> f(r, e)}
}

/**
 * Forget about all watched keys
 * Complexity: O(1)
 * @return always `OK`.
 */
inline fun Connection.unwatch(crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 1)
    writeBulkString(target, kw_unwatch)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Wait for the synchronous replication of all the write commands sent in the context of the current connection
 * Complexity: O(1)
 * @return The command returns the number of slaves reached by all the writes performed in the context of the current connection.
 */
inline fun Connection.wait(numslaves: Long, timeout: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_wait)
    writeBulkString(target, numslaves.toString())
    writeBulkString(target, timeout.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Watch the given keys to determine execution of the MULTI/EXEC block
 * Complexity: O(1) for every key.
 * @return always `OK`.
 */
inline fun Connection.watch(key: String, crossinline f: (String?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_watch)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}
}

/**
 * Add one or more members to a sorted set, or update its score if it already exists
 * Complexity: O(log(N)) for each item added, where N is the number of elements in the sorted set.
 * @return specifically:
* The number of elements added to the sorted sets, not including elements
  already existing for which the score was updated.
If the `INCR` option is specified, the return value will be @bulk-string-reply:
* the new score of `member` (a double precision floating point number), represented as string.
@history
* `>= 2.4`: Accepts multiple elements.
  In Redis versions older than 2.4 it was possible to add or update a single
  member per call.
 */
inline fun Connection.zadd(key: String, condition: String, change: String, increment: String, score: Any, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 7)
    writeBulkString(target, kw_zadd)
    writeBulkString(target, key)
    writeBulkString(target, condition)
    writeBulkString(target, change)
    writeBulkString(target, increment)
    writeBulkString(target, score.toString())
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Get the number of members in a sorted set
 * Complexity: O(1)
 * @return the cardinality (number of elements) of the sorted set, or `0`
if `key` does not exist.
 */
inline fun Connection.zcard(key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_zcard)
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Count the members in a sorted set with scores within the given values
 * Complexity: O(log(N)) with N being the number of elements in the sorted set.
 * @return the number of elements in the specified score range.
 */
inline fun Connection.zcount(key: String, min: Any, max: Any, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zcount)
    writeBulkString(target, key)
    writeBulkString(target, min.toString())
    writeBulkString(target, max.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Increment the score of a member in a sorted set
 * Complexity: O(log(N)) where N is the number of elements in the sorted set.
 * @return the new score of `member` (a double precision floating point
number), represented as string.
 */
inline fun Connection.zincrby(key: String, increment: Long, member: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zincrby)
    writeBulkString(target, key)
    writeBulkString(target, increment.toString())
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Intersect multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N*K)+O(M*log(M)) worst case with N being the smallest input sorted set, K being the number of input sorted sets and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zinterstore(destination: String, numkeys: Long, key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zinterstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Intersect multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N*K)+O(M*log(M)) worst case with N being the smallest input sorted set, K being the number of input sorted sets and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zinterstore_weights(destination: String, numkeys: Long, key: String, weights_weight: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zinterstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, kw_weights)
    writeBulkString(target, weights_weight.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Intersect multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N*K)+O(M*log(M)) worst case with N being the smallest input sorted set, K being the number of input sorted sets and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zinterstore_aggregate(destination: String, numkeys: Long, key: String, aggregate_aggregate: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zinterstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, kw_aggregate)
    writeBulkString(target, aggregate_aggregate)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Intersect multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N*K)+O(M*log(M)) worst case with N being the smallest input sorted set, K being the number of input sorted sets and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zinterstore_weights_aggregate(destination: String, numkeys: Long, key: String, weights_weight: Long, aggregate_aggregate: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zinterstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, kw_weights)
    writeBulkString(target, weights_weight.toString())
    writeBulkString(target, kw_aggregate)
    writeBulkString(target, aggregate_aggregate)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Count the number of members in a sorted set between a given lexicographical range
 * Complexity: O(log(N)) with N being the number of elements in the sorted set.
 * @return the number of elements in the specified score range.
 */
inline fun Connection.zlexcount(key: String, min: String, max: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zlexcount)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Return a range of members in a sorted set, by index
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements returned.
 * @return list of elements in the specified range (optionally with
their scores, in case the `WITHSCORES` option is given).
 */
inline fun Connection.zrange(key: String, start: Long, stop: Long, withscores: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_zrange)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, stop.toString())
    writeBulkString(target, withscores)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by lexicographical range
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range.
 */
inline fun Connection.zrangebylex(key: String, min: String, max: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zrangebylex)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by lexicographical range
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range.
 */
inline fun Connection.zrangebylex_limit(key: String, min: String, max: String, limit_offset: Long, limit_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zrangebylex)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by lexicographical range, ordered from higher to lower strings.
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range.
 */
inline fun Connection.zrevrangebylex(key: String, max: String, min: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zrevrangebylex)
    writeBulkString(target, key)
    writeBulkString(target, max)
    writeBulkString(target, min)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by lexicographical range, ordered from higher to lower strings.
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range.
 */
inline fun Connection.zrevrangebylex_limit(key: String, max: String, min: String, limit_offset: Long, limit_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zrevrangebylex)
    writeBulkString(target, key)
    writeBulkString(target, max)
    writeBulkString(target, min)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by score
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range (optionally
with their scores).
 */
inline fun Connection.zrangebyscore(key: String, min: Any, max: Any, withscores: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_zrangebyscore)
    writeBulkString(target, key)
    writeBulkString(target, min.toString())
    writeBulkString(target, max.toString())
    writeBulkString(target, withscores)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by score
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range (optionally
with their scores).
 */
inline fun Connection.zrangebyscore_limit(key: String, min: Any, max: Any, withscores: String, limit_offset: Long, limit_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_zrangebyscore)
    writeBulkString(target, key)
    writeBulkString(target, min.toString())
    writeBulkString(target, max.toString())
    writeBulkString(target, withscores)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Determine the index of a member in a sorted set
 * Complexity: O(log(N))
 * @return * If `member` does not exist in the sorted set or `key` does not exist,
  @bulk-string-reply: `nil`.
 */
inline fun Connection.zrank(key: String, member: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zrank)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(r, e)}
}

/**
 * Remove one or more members from a sorted set
 * Complexity: O(M*log(N)) with N being the number of elements in the sorted set and M the number of elements to be removed.
 * @return specifically:
* The number of members removed from the sorted set, not including non existing
  members.
@history
* `>= 2.4`: Accepts multiple elements.
  In Redis versions older than 2.4 it was possible to remove a single member per
  call.
 */
inline fun Connection.zrem(key: String, member: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zrem)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Remove all members in a sorted set between the given lexicographical range
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation.
 * @return the number of elements removed.
 */
inline fun Connection.zremrangebylex(key: String, min: String, max: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zremrangebylex)
    writeBulkString(target, key)
    writeBulkString(target, min)
    writeBulkString(target, max)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Remove all members in a sorted set within the given indexes
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation.
 * @return the number of elements removed.
 */
inline fun Connection.zremrangebyrank(key: String, start: Long, stop: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zremrangebyrank)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, stop.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Remove all members in a sorted set within the given scores
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation.
 * @return the number of elements removed.
 */
inline fun Connection.zremrangebyscore(key: String, min: Any, max: Any, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zremrangebyscore)
    writeBulkString(target, key)
    writeBulkString(target, min.toString())
    writeBulkString(target, max.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Return a range of members in a sorted set, by index, with scores ordered from high to low
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements returned.
 * @return list of elements in the specified range (optionally with
their scores).
 */
inline fun Connection.zrevrange(key: String, start: Long, stop: Long, withscores: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_zrevrange)
    writeBulkString(target, key)
    writeBulkString(target, start.toString())
    writeBulkString(target, stop.toString())
    writeBulkString(target, withscores)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by score, with scores ordered from high to low
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range (optionally
with their scores).
 */
inline fun Connection.zrevrangebyscore(key: String, max: Any, min: Any, withscores: String, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_zrevrangebyscore)
    writeBulkString(target, key)
    writeBulkString(target, max.toString())
    writeBulkString(target, min.toString())
    writeBulkString(target, withscores)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Return a range of members in a sorted set, by score, with scores ordered from high to low
 * Complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements being returned. If M is constant (e.g. always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).
 * @return list of elements in the specified score range (optionally
with their scores).
 */
inline fun Connection.zrevrangebyscore_limit(key: String, max: Any, min: Any, withscores: String, limit_offset: Long, limit_count: Long, crossinline f: (Array<Response>?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 5)
    writeBulkString(target, kw_zrevrangebyscore)
    writeBulkString(target, key)
    writeBulkString(target, max.toString())
    writeBulkString(target, min.toString())
    writeBulkString(target, withscores)
    writeBulkString(target, kw_limit)
    writeBulkString(target, limit_offset.toString())
    writeBulkString(target, limit_count.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}
}

/**
 * Determine the index of a member in a sorted set, with scores ordered from high to low
 * Complexity: O(log(N))
 * @return * If `member` does not exist in the sorted set or `key` does not exist,
  @bulk-string-reply: `nil`.
 */
inline fun Connection.zrevrank(key: String, member: String, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zrevrank)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(r, e)}
}

/**
 * Get the score associated with the given member in a sorted set
 * Complexity: O(1)
 * @return the score of `member` (a double precision floating point number),
represented as string.
 */
inline fun Connection.zscore(key: String, member: String, crossinline f: (ByteBuf?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zscore)
    writeBulkString(target, key)
    writeBulkString(target, member)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}
}

/**
 * Add multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N)+O(M log(M)) with N being the sum of the sizes of the input sorted sets, and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zunionstore(destination: String, numkeys: Long, key: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zunionstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Add multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N)+O(M log(M)) with N being the sum of the sizes of the input sorted sets, and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zunionstore_weights(destination: String, numkeys: Long, key: String, weights_weight: Long, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zunionstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, kw_weights)
    writeBulkString(target, weights_weight.toString())
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Add multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N)+O(M log(M)) with N being the sum of the sizes of the input sorted sets, and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zunionstore_aggregate(destination: String, numkeys: Long, key: String, aggregate_aggregate: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zunionstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, kw_aggregate)
    writeBulkString(target, aggregate_aggregate)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Add multiple sorted sets and store the resulting sorted set in a new key
 * Complexity: O(N)+O(M log(M)) with N being the sum of the sizes of the input sorted sets, and M being the number of elements in the resulting sorted set.
 * @return the number of elements in the resulting sorted set at
`destination`.
 */
inline fun Connection.zunionstore_weights_aggregate(destination: String, numkeys: Long, key: String, weights_weight: Long, aggregate_aggregate: String, crossinline f: (Long?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 4)
    writeBulkString(target, kw_zunionstore)
    writeBulkString(target, destination)
    writeBulkString(target, numkeys.toString())
    writeBulkString(target, key)
    writeBulkString(target, kw_weights)
    writeBulkString(target, weights_weight.toString())
    writeBulkString(target, kw_aggregate)
    writeBulkString(target, aggregate_aggregate)
    command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}
}

/**
 * Incrementally iterate the keys space
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection.
 */
inline fun Connection.scan(cursor: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_scan)
    writeBulkString(target, cursor.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate the keys space
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection.
 */
inline fun Connection.scan_match(cursor: Long, match_pattern: Any, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_scan)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate the keys space
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection.
 */
inline fun Connection.scan_count(cursor: Long, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_scan)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate the keys space
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection.
 */
inline fun Connection.scan_match_count(cursor: Long, match_pattern: Any, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 2)
    writeBulkString(target, kw_scan)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate Set elements
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.sscan(key: String, cursor: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate Set elements
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.sscan_match(key: String, cursor: Long, match_pattern: Any, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate Set elements
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.sscan_count(key: String, cursor: Long, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate Set elements
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.sscan_match_count(key: String, cursor: Long, match_pattern: Any, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_sscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate hash fields and associated values
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.hscan(key: String, cursor: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate hash fields and associated values
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.hscan_match(key: String, cursor: Long, match_pattern: Any, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate hash fields and associated values
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.hscan_count(key: String, cursor: Long, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate hash fields and associated values
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.hscan_match_count(key: String, cursor: Long, match_pattern: Any, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_hscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate sorted sets elements and associated scores
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.zscan(key: String, cursor: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate sorted sets elements and associated scores
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.zscan_match(key: String, cursor: Long, match_pattern: Any, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate sorted sets elements and associated scores
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.zscan_count(key: String, cursor: Long, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}

/**
 * Incrementally iterate sorted sets elements and associated scores
 * Complexity: O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..
 */
inline fun Connection.zscan_match_count(key: String, cursor: Long, match_pattern: Any, count_count: Long, crossinline f: (Response?, Throwable?) -> Unit) {
    val target = ByteBufAllocator.DEFAULT.buffer(32)
    writeArray(target, 3)
    writeBulkString(target, kw_zscan)
    writeBulkString(target, key)
    writeBulkString(target, cursor.toString())
    writeBulkString(target, kw_match)
    writeBulkString(target, match_pattern.toString())
    writeBulkString(target, kw_count)
    writeBulkString(target, count_count.toString())
    command(target) {r, e -> f(r, e)}
}


val kw_cluster = "CLUSTER".toByteArray(Charsets.UTF_8)
val kw_getkeysinslot = "GETKEYSINSLOT".toByteArray(Charsets.UTF_8)
val kw_lastsave = "LASTSAVE".toByteArray(Charsets.UTF_8)
val kw_px = "PX".toByteArray(Charsets.UTF_8)
val kw_zunionstore = "ZUNIONSTORE".toByteArray(Charsets.UTF_8)
val kw_del = "DEL".toByteArray(Charsets.UTF_8)
val kw_echo = "ECHO".toByteArray(Charsets.UTF_8)
val kw_psubscribe = "PSUBSCRIBE".toByteArray(Charsets.UTF_8)
val kw_sinterstore = "SINTERSTORE".toByteArray(Charsets.UTF_8)
val kw_set_config_epoch = "SET-CONFIG-EPOCH".toByteArray(Charsets.UTF_8)
val kw_randomkey = "RANDOMKEY".toByteArray(Charsets.UTF_8)
val kw_setex = "SETEX".toByteArray(Charsets.UTF_8)
val kw_readonly = "READONLY".toByteArray(Charsets.UTF_8)
val kw_zlexcount = "ZLEXCOUNT".toByteArray(Charsets.UTF_8)
val kw_unsubscribe = "UNSUBSCRIBE".toByteArray(Charsets.UTF_8)
val kw_brpoplpush = "BRPOPLPUSH".toByteArray(Charsets.UTF_8)
val kw_bitcount = "BITCOUNT".toByteArray(Charsets.UTF_8)
val kw_llen = "LLEN".toByteArray(Charsets.UTF_8)
val kw_punsubscribe = "PUNSUBSCRIBE".toByteArray(Charsets.UTF_8)
val kw_hvals = "HVALS".toByteArray(Charsets.UTF_8)
val kw_rewrite = "REWRITE".toByteArray(Charsets.UTF_8)
val kw_flushdb = "FLUSHDB".toByteArray(Charsets.UTF_8)
val kw_saveconfig = "SAVECONFIG".toByteArray(Charsets.UTF_8)
val kw_geoadd = "GEOADD".toByteArray(Charsets.UTF_8)
val kw_expire = "EXPIRE".toByteArray(Charsets.UTF_8)
val kw_bitop = "BITOP".toByteArray(Charsets.UTF_8)
val kw_object = "OBJECT".toByteArray(Charsets.UTF_8)
val kw_role = "ROLE".toByteArray(Charsets.UTF_8)
val kw_smove = "SMOVE".toByteArray(Charsets.UTF_8)
val kw_lset = "LSET".toByteArray(Charsets.UTF_8)
val kw_scan = "SCAN".toByteArray(Charsets.UTF_8)
val kw_blpop = "BLPOP".toByteArray(Charsets.UTF_8)
val kw_rpoplpush = "RPOPLPUSH".toByteArray(Charsets.UTF_8)
val kw_lpop = "LPOP".toByteArray(Charsets.UTF_8)
val kw_by = "BY".toByteArray(Charsets.UTF_8)
val kw_persist = "PERSIST".toByteArray(Charsets.UTF_8)
val kw_bgsave = "BGSAVE".toByteArray(Charsets.UTF_8)
val kw_replicate = "REPLICATE".toByteArray(Charsets.UTF_8)
val kw_georadius = "GEORADIUS".toByteArray(Charsets.UTF_8)
val kw_set = "SET".toByteArray(Charsets.UTF_8)
val kw_srandmember = "SRANDMEMBER".toByteArray(Charsets.UTF_8)
val kw_incr = "INCR".toByteArray(Charsets.UTF_8)
val kw_segfault = "SEGFAULT".toByteArray(Charsets.UTF_8)
val kw_hexists = "HEXISTS".toByteArray(Charsets.UTF_8)
val kw_store = "STORE".toByteArray(Charsets.UTF_8)
val kw_georadiusbymember = "GEORADIUSBYMEMBER".toByteArray(Charsets.UTF_8)
val kw_pexpire = "PEXPIRE".toByteArray(Charsets.UTF_8)
val kw_zcard = "ZCARD".toByteArray(Charsets.UTF_8)
val kw_sinter = "SINTER".toByteArray(Charsets.UTF_8)
val kw_meet = "MEET".toByteArray(Charsets.UTF_8)
val kw_watch = "WATCH".toByteArray(Charsets.UTF_8)
val kw_quit = "QUIT".toByteArray(Charsets.UTF_8)
val kw_zrangebyscore = "ZRANGEBYSCORE".toByteArray(Charsets.UTF_8)
val kw_sdiff = "SDIFF".toByteArray(Charsets.UTF_8)
val kw_config = "CONFIG".toByteArray(Charsets.UTF_8)
val kw_unwatch = "UNWATCH".toByteArray(Charsets.UTF_8)
val kw_getbit = "GETBIT".toByteArray(Charsets.UTF_8)
val kw_scard = "SCARD".toByteArray(Charsets.UTF_8)
val kw_resetstat = "RESETSTAT".toByteArray(Charsets.UTF_8)
val kw_strlen = "STRLEN".toByteArray(Charsets.UTF_8)
val kw_hdel = "HDEL".toByteArray(Charsets.UTF_8)
val kw_delslots = "DELSLOTS".toByteArray(Charsets.UTF_8)
val kw_geohash = "GEOHASH".toByteArray(Charsets.UTF_8)
val kw_hincrbyfloat = "HINCRBYFLOAT".toByteArray(Charsets.UTF_8)
val kw_reply = "REPLY".toByteArray(Charsets.UTF_8)
val kw_lrem = "LREM".toByteArray(Charsets.UTF_8)
val kw_hlen = "HLEN".toByteArray(Charsets.UTF_8)
val kw_decr = "DECR".toByteArray(Charsets.UTF_8)
val kw_info = "INFO".toByteArray(Charsets.UTF_8)
val kw_ltrim = "LTRIM".toByteArray(Charsets.UTF_8)
val kw_count = "COUNT".toByteArray(Charsets.UTF_8)
val kw_slaveof = "SLAVEOF".toByteArray(Charsets.UTF_8)
val kw_rpop = "RPOP".toByteArray(Charsets.UTF_8)
val kw_list = "LIST".toByteArray(Charsets.UTF_8)
val kw_sync = "SYNC".toByteArray(Charsets.UTF_8)
val kw_pfadd = "PFADD".toByteArray(Charsets.UTF_8)
val kw_slots = "SLOTS".toByteArray(Charsets.UTF_8)
val kw_eval = "EVAL".toByteArray(Charsets.UTF_8)
val kw_ex = "EX".toByteArray(Charsets.UTF_8)
val kw_linsert = "LINSERT".toByteArray(Charsets.UTF_8)
val kw_failover = "FAILOVER".toByteArray(Charsets.UTF_8)
val kw_pfcount = "PFCOUNT".toByteArray(Charsets.UTF_8)
val kw_exec = "EXEC".toByteArray(Charsets.UTF_8)
val kw_migrate = "MIGRATE".toByteArray(Charsets.UTF_8)
val kw_incrby = "INCRBY".toByteArray(Charsets.UTF_8)
val kw_getset = "GETSET".toByteArray(Charsets.UTF_8)
val kw_hset = "HSET".toByteArray(Charsets.UTF_8)
val kw_spop = "SPOP".toByteArray(Charsets.UTF_8)
val kw_pexpireat = "PEXPIREAT".toByteArray(Charsets.UTF_8)
val kw_hmset = "HMSET".toByteArray(Charsets.UTF_8)
val kw_flush = "FLUSH".toByteArray(Charsets.UTF_8)
val kw_load = "LOAD".toByteArray(Charsets.UTF_8)
val kw_zrevrangebyscore = "ZREVRANGEBYSCORE".toByteArray(Charsets.UTF_8)
val kw_client = "CLIENT".toByteArray(Charsets.UTF_8)
val kw_sunion = "SUNION".toByteArray(Charsets.UTF_8)
val kw_hincrby = "HINCRBY".toByteArray(Charsets.UTF_8)
val kw_geopos = "GEOPOS".toByteArray(Charsets.UTF_8)
val kw_mset = "MSET".toByteArray(Charsets.UTF_8)
val kw_monitor = "MONITOR".toByteArray(Charsets.UTF_8)
val kw_zscore = "ZSCORE".toByteArray(Charsets.UTF_8)
val kw_lrange = "LRANGE".toByteArray(Charsets.UTF_8)
val kw_hstrlen = "HSTRLEN".toByteArray(Charsets.UTF_8)
val kw_nodes = "NODES".toByteArray(Charsets.UTF_8)
val kw_pfmerge = "PFMERGE".toByteArray(Charsets.UTF_8)
val kw_publish = "PUBLISH".toByteArray(Charsets.UTF_8)
val kw_rpush = "RPUSH".toByteArray(Charsets.UTF_8)
val kw_readwrite = "READWRITE".toByteArray(Charsets.UTF_8)
val kw_time = "TIME".toByteArray(Charsets.UTF_8)
val kw_append = "APPEND".toByteArray(Charsets.UTF_8)
val kw_sismember = "SISMEMBER".toByteArray(Charsets.UTF_8)
val kw_discard = "DISCARD".toByteArray(Charsets.UTF_8)
val kw_select = "SELECT".toByteArray(Charsets.UTF_8)
val kw_zcount = "ZCOUNT".toByteArray(Charsets.UTF_8)
val kw_skipme = "SKIPME".toByteArray(Charsets.UTF_8)
val kw_sunionstore = "SUNIONSTORE".toByteArray(Charsets.UTF_8)
val kw_zinterstore = "ZINTERSTORE".toByteArray(Charsets.UTF_8)
val kw_hscan = "HSCAN".toByteArray(Charsets.UTF_8)
val kw_type = "TYPE".toByteArray(Charsets.UTF_8)
val kw_multi = "MULTI".toByteArray(Charsets.UTF_8)
val kw_count_failure_reports = "COUNT-FAILURE-REPORTS".toByteArray(Charsets.UTF_8)
val kw_incrbyfloat = "INCRBYFLOAT".toByteArray(Charsets.UTF_8)
val kw_flushall = "FLUSHALL".toByteArray(Charsets.UTF_8)
val kw_id = "ID".toByteArray(Charsets.UTF_8)
val kw_zscan = "ZSCAN".toByteArray(Charsets.UTF_8)
val kw_lpushx = "LPUSHX".toByteArray(Charsets.UTF_8)
val kw_bitpos = "BITPOS".toByteArray(Charsets.UTF_8)
val kw_slaves = "SLAVES".toByteArray(Charsets.UTF_8)
val kw_setnx = "SETNX".toByteArray(Charsets.UTF_8)
val kw_evalsha = "EVALSHA".toByteArray(Charsets.UTF_8)
val kw_script = "SCRIPT".toByteArray(Charsets.UTF_8)
val kw_geodist = "GEODIST".toByteArray(Charsets.UTF_8)
val kw_reset = "RESET".toByteArray(Charsets.UTF_8)
val kw_countkeysinslot = "COUNTKEYSINSLOT".toByteArray(Charsets.UTF_8)
val kw_setslot = "SETSLOT".toByteArray(Charsets.UTF_8)
val kw_dbsize = "DBSIZE".toByteArray(Charsets.UTF_8)
val kw_wait = "WAIT".toByteArray(Charsets.UTF_8)
val kw_ping = "PING".toByteArray(Charsets.UTF_8)
val kw_pttl = "PTTL".toByteArray(Charsets.UTF_8)
val kw_save = "SAVE".toByteArray(Charsets.UTF_8)
val kw_zrank = "ZRANK".toByteArray(Charsets.UTF_8)
val kw_expireat = "EXPIREAT".toByteArray(Charsets.UTF_8)
val kw_zremrangebylex = "ZREMRANGEBYLEX".toByteArray(Charsets.UTF_8)
val kw_get = "GET".toByteArray(Charsets.UTF_8)
val kw_addslots = "ADDSLOTS".toByteArray(Charsets.UTF_8)
val kw_bgrewriteaof = "BGREWRITEAOF".toByteArray(Charsets.UTF_8)
val kw_decrby = "DECRBY".toByteArray(Charsets.UTF_8)
val kw_setbit = "SETBIT".toByteArray(Charsets.UTF_8)
val kw_kill = "KILL".toByteArray(Charsets.UTF_8)
val kw_getname = "GETNAME".toByteArray(Charsets.UTF_8)
val kw_srem = "SREM".toByteArray(Charsets.UTF_8)
val kw_getrange = "GETRANGE".toByteArray(Charsets.UTF_8)
val kw_rename = "RENAME".toByteArray(Charsets.UTF_8)
val kw_zrevrank = "ZREVRANK".toByteArray(Charsets.UTF_8)
val kw_exists = "EXISTS".toByteArray(Charsets.UTF_8)
val kw_setrange = "SETRANGE".toByteArray(Charsets.UTF_8)
val kw_sadd = "SADD".toByteArray(Charsets.UTF_8)
val kw_zrevrangebylex = "ZREVRANGEBYLEX".toByteArray(Charsets.UTF_8)
val kw_zrevrange = "ZREVRANGE".toByteArray(Charsets.UTF_8)
val kw_sdiffstore = "SDIFFSTORE".toByteArray(Charsets.UTF_8)
val kw_zincrby = "ZINCRBY".toByteArray(Charsets.UTF_8)
val kw_auth = "AUTH".toByteArray(Charsets.UTF_8)
val kw_rpushx = "RPUSHX".toByteArray(Charsets.UTF_8)
val kw_psetex = "PSETEX".toByteArray(Charsets.UTF_8)
val kw_limit = "LIMIT".toByteArray(Charsets.UTF_8)
val kw_brpop = "BRPOP".toByteArray(Charsets.UTF_8)
val kw_pubsub = "PUBSUB".toByteArray(Charsets.UTF_8)
val kw_lindex = "LINDEX".toByteArray(Charsets.UTF_8)
val kw_lpush = "LPUSH".toByteArray(Charsets.UTF_8)
val kw_zrange = "ZRANGE".toByteArray(Charsets.UTF_8)
val kw_slowlog = "SLOWLOG".toByteArray(Charsets.UTF_8)
val kw_sort = "SORT".toByteArray(Charsets.UTF_8)
val kw_weights = "WEIGHTS".toByteArray(Charsets.UTF_8)
val kw_pause = "PAUSE".toByteArray(Charsets.UTF_8)
val kw_keyslot = "KEYSLOT".toByteArray(Charsets.UTF_8)
val kw_setname = "SETNAME".toByteArray(Charsets.UTF_8)
val kw_forget = "FORGET".toByteArray(Charsets.UTF_8)
val kw_hkeys = "HKEYS".toByteArray(Charsets.UTF_8)
val kw_hsetnx = "HSETNX".toByteArray(Charsets.UTF_8)
val kw_zrangebylex = "ZRANGEBYLEX".toByteArray(Charsets.UTF_8)
val kw_shutdown = "SHUTDOWN".toByteArray(Charsets.UTF_8)
val kw_keys = "KEYS".toByteArray(Charsets.UTF_8)
val kw_zremrangebyscore = "ZREMRANGEBYSCORE".toByteArray(Charsets.UTF_8)
val kw_aggregate = "AGGREGATE".toByteArray(Charsets.UTF_8)
val kw_renamenx = "RENAMENX".toByteArray(Charsets.UTF_8)
val kw_zrem = "ZREM".toByteArray(Charsets.UTF_8)
val kw_getkeys = "GETKEYS".toByteArray(Charsets.UTF_8)
val kw_dump = "DUMP".toByteArray(Charsets.UTF_8)
val kw_msetnx = "MSETNX".toByteArray(Charsets.UTF_8)
val kw_addr = "ADDR".toByteArray(Charsets.UTF_8)
val kw_hmget = "HMGET".toByteArray(Charsets.UTF_8)
val kw_hget = "HGET".toByteArray(Charsets.UTF_8)
val kw_zadd = "ZADD".toByteArray(Charsets.UTF_8)
val kw_move = "MOVE".toByteArray(Charsets.UTF_8)
val kw_debug = "DEBUG".toByteArray(Charsets.UTF_8)
val kw_hgetall = "HGETALL".toByteArray(Charsets.UTF_8)
val kw_restore = "RESTORE".toByteArray(Charsets.UTF_8)
val kw_zremrangebyrank = "ZREMRANGEBYRANK".toByteArray(Charsets.UTF_8)
val kw_subscribe = "SUBSCRIBE".toByteArray(Charsets.UTF_8)
val kw_match = "MATCH".toByteArray(Charsets.UTF_8)
val kw_ttl = "TTL".toByteArray(Charsets.UTF_8)
val kw_command = "COMMAND".toByteArray(Charsets.UTF_8)
val kw_smembers = "SMEMBERS".toByteArray(Charsets.UTF_8)
val kw_mget = "MGET".toByteArray(Charsets.UTF_8)
val kw_sscan = "SSCAN".toByteArray(Charsets.UTF_8)
