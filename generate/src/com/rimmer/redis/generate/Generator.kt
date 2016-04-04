package com.rimmer.redis.generate

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.*
import java.net.URL

enum class ReturnType {
    SimpleString, BulkString, Integer, Array, Any
}

enum class ArgType {
    String, Key, Int, Any
}

class CommandArg(val name: String, val type: ArgType, val optional: Boolean)
class Command(val name: String, val additionalNames: List<String>, val summary: String, val complexity: String?, val returnType: ReturnType, val returnDesc: String, val args: List<CommandArg>)

class Builder(val writer: Writer) {
    var indent = 0

    inline fun withLevel(f: Builder.() -> Unit) {
        indent += 1
        f()
        indent -= 1
    }

    fun line(s: String) {
        startLine()
        append(s)
        newLine()
    }

    fun newLine() {
        writer.append('\n')
    }

    fun startLine() {
        var i = 0
        while(i < indent) {
            writer.append("    ")
            i++
        }
    }

    fun append(s: String) {
        writer.append(s)
    }
}

fun generateCommands(command: (Command) -> Unit) {
    val commands = JsonParser().parse(BufferedReader(InputStreamReader(
        URL("https://raw.github.com/antirez/redis-doc/master/commands.json").openStream()
    ))).asJsonObject

    commands.entrySet().forEach {
        val reader = BufferedReader(InputStreamReader(
            URL("https://raw.githubusercontent.com/antirez/redis-doc/master/commands/${it.key.replace(' ', '-').toLowerCase()}.md").openStream()
        ))

        val description = StringBuilder()
        var returnType = ReturnType.Any
        val returnDesc = StringBuilder()

        while(true) {
            val line = reader.readLine() ?: break
            if(line.startsWith("@return")) {
                val first = reader.readLine() ?: continue
                val type = if(first.isEmpty()) reader.readLine() ?: continue else first
                if(type.startsWith("@array-reply")) {
                    returnType = ReturnType.Array
                    val d = type.removePrefix("@array-reply")
                    if(d.length > 3) returnDesc.appendln(d.drop(2))
                } else if(type.startsWith("@bulk-string-reply")) {
                    returnType = ReturnType.BulkString
                    val d = type.removePrefix("@bulk-string-reply")
                    if(d.length > 3) returnDesc.appendln(d.drop(2))
                } else if(type.startsWith("@integer-reply")) {
                    returnType = ReturnType.Integer
                    val d = type.removePrefix("@integer-reply")
                    if(d.length > 3) returnDesc.appendln(d.drop(2))
                } else if(type.startsWith("@simple-string-reply")) {
                    returnType = ReturnType.SimpleString
                    val d = type.removePrefix("@simple-string-reply")
                    if(d.length > 3) returnDesc.appendln(d.drop(2))
                }

                while(true) {
                    val desc = reader.readLine() ?: break
                    if(desc.startsWith("@examples")) break
                    if(desc.length > 1) returnDesc.appendln(desc)
                }

                break
            } else if(line.length > 3) {
                description.appendln(" * $line")
            }
        }

        val c = it.value.asJsonObject
        val ps = c["arguments"]?.asJsonArray

        val complexity = if(c.has("complexity")) {
            c["complexity"].asString
        } else null

        val args = ps?.flatMap {
            val p = it.asJsonObject
            val n = p["name"]
            val t = p["type"]
            val optional = p["optional"]?.asBoolean ?: false

            val getType = {t: JsonElement ->
                when(t.asString) {
                    "string" -> ArgType.String
                    "key" -> ArgType.String
                    "enum" -> ArgType.String
                    "integer" -> ArgType.Int
                    else -> ArgType.Any
                }
            }

            val name = {
                if(t.isJsonArray) {
                    val names = n.asJsonArray
                    val types = t.asJsonArray
                    names.zip(types).map {
                        CommandArg(it.first.asString, getType(it.second), optional)
                    }
                } else {
                    listOf(CommandArg(n.asString, getType(t), optional))
                }
            }

            val first = if(p.has("command")) {
                val cmd = p["command"].asString
                listOf(CommandArg(cmd, ArgType.String, optional))
            } else null

            if(p.has("name")) {
                if(first != null) first + name() else name()
            } else first!!
        } ?: emptyList<CommandArg>()

        val names = it.key.toLowerCase().split(' ')
        val summary = c["summary"].asString

        command(Command(names.first(), names.drop(1), summary, complexity, returnType, returnDesc.toString(), args))
    }
}

fun generateCallbackCommand(builder: Builder) = { c: Command ->
    builder.line("/**")
    builder.line(" * ${c.summary.trim()}")
    if(c.complexity != null) {
        builder.line(" * Complexity: ${c.complexity}")
    }

    val ret = c.returnDesc.trim()
    if(ret.isNotEmpty()) {
        builder.line(" * @return $ret")
    }
    builder.line(" */")
    builder.startLine()
    builder.append("inline fun Connection.${(listOf(c.name.toLowerCase()) + c.additionalNames).joinToString("_")}(")

    c.args.forEach {
        val type = when(it.type) {
            ArgType.String -> "String"
            ArgType.Key -> "String"
            ArgType.Int -> "Long"
            else -> "Any"
        }
        builder.append("${it.name.filter {it.isJavaIdentifierPart()}}: $type, ")
    }

    when(c.returnType) {
        ReturnType.SimpleString -> builder.append("crossinline f: (String?, Throwable?) -> Unit")
        ReturnType.Integer -> builder.append("crossinline f: (Long?, Throwable?) -> Unit")
        ReturnType.BulkString -> builder.append("crossinline f: (ByteBuf?, Throwable?) -> Unit")
        ReturnType.Array -> builder.append("crossinline f: (Array<Response>?, Throwable?) -> Unit")
        ReturnType.Any -> builder.append("crossinline f: (Response?, Throwable?) -> Unit")
    }
    builder.append(") {")
    builder.newLine()

    builder.withLevel {
        builder.line("val target = ByteBufAllocator.DEFAULT.buffer(32)")
        builder.line("writeArray(target, ${c.args.size + c.additionalNames.size + 1})")
        builder.line("writeBulkString(target, Command.${c.name.toUpperCase()}.bytes)")

        for(n in c.additionalNames) {
            builder.line("writeBulkString(target, Keyword.${n.toUpperCase()}.bytes)")
        }

        for(a in c.args) {
            val convert = when(a.type) {
                ArgType.String, ArgType.Key -> a.name.filter {it.isJavaIdentifierPart()}
                else -> "${a.name.filter {it.isJavaIdentifierPart()}}.toString()"
            }
            builder.line("writeBulkString(target, $convert)")
        }

        when(c.returnType) {
            ReturnType.SimpleString -> builder.line("command(target) {r, e -> f(if(r == null || r.isNull) null else r.string, e)}")
            ReturnType.Integer -> builder.line("command(target) {r, e -> f(if(r == null || r.isNull) null else r.int, e)}")
            ReturnType.BulkString -> builder.line("command(target) {r, e -> f(if(r == null || r.isNull) null else r.data, e)}")
            ReturnType.Array -> builder.line("command(target) {r, e -> f(if(r == null || r.isNull) null else r.array, e)}")
            ReturnType.Any -> builder.line("command(target) {r, e -> f(r, e)}")
        }
    }
    builder.line("}")
    builder.newLine()
}

fun generateCallbackCommands(target: Writer, targetPackage: String) {
    val builder = Builder(target)
    builder.line("package $targetPackage")
    builder.newLine()
    builder.line("import com.rimmer.redis.protocol.Connection")
    builder.line("import com.rimmer.redis.protocol.Response")
    builder.line("import com.rimmer.redis.protocol.writeArray")
    builder.line("import com.rimmer.redis.protocol.writeBulkString")
    builder.line("import io.netty.buffer.ByteBufAllocator")
    builder.line("import io.netty.buffer.ByteBuf")
    builder.newLine()

    generateCommands(generateCallbackCommand(builder))
}

fun main(args: Array<String>) {
    File("src/com/rimmer/redis/command/").mkdirs()
    val writer = FileWriter("src/com/rimmer/redis/command/Commands.kt")
    generateCallbackCommands(writer, "com.rimmer.redis.command")
    writer.close()
}