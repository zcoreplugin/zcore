package me.zavdav.zcore.util

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.user.OfflineUser
import me.zavdav.zcore.data.user.User
import java.math.BigDecimal
import java.util.regex.Pattern

internal class WordArgument(val maxLength: Int = Int.MAX_VALUE) : ArgumentType<String> {
    override fun parse(reader: StringReader): String {
        val arg = reader.readArgument()
        return if (arg.length <= maxLength)
            arg
        else
            throw IllegalArgumentException("Expected max length $maxLength, got length $arg.length")
    }
}

internal class GreedyStringArgument(val maxLength: Int = Int.MAX_VALUE) : ArgumentType<String> {
    override fun parse(reader: StringReader): String {
        val arg = reader.remaining
        reader.cursor = reader.totalLength
        return if (arg.length <= maxLength)
            arg
        else
            throw IllegalArgumentException("Expected max length $maxLength, got length ${arg.length}")
    }
}

internal class IntArgument(
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE
) : ArgumentType<Int> {
    override fun parse(reader: StringReader): Int {
        val arg = reader.readArgument()
        return try {
            val number = arg.toInt()
            if (number > max)
                throw IllegalArgumentException("Expected value <= $max, got $number")
            else if (number < min)
                throw IllegalArgumentException("Expected value >= $min, got $number")
            else
                number
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("Expected int, got \"$arg\"")
        }
    }
}

internal class LongArgument(
    val min: Long = Long.MIN_VALUE,
    val max: Long = Long.MAX_VALUE
) : ArgumentType<Long> {
    override fun parse(reader: StringReader): Long {
        val arg = reader.readArgument()
        return try {
            val number = arg.toLong()
            if (number > max)
                throw IllegalArgumentException("Expected value <= $max, got $number")
            else if (number < min)
                throw IllegalArgumentException("Expected value >= $min, got $number")
            else
                number
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("Expected long, got \"$arg\"")
        }
    }
}

internal class FloatArgument(
    val min: Float = -Float.MAX_VALUE,
    val max: Float = Float.MAX_VALUE
) : ArgumentType<Float> {
    override fun parse(reader: StringReader): Float {
        val arg = reader.readArgument()
        return try {
            val number = arg.toFloat()
            if (number > max)
                throw IllegalArgumentException("Expected value <= $max, got $number")
            else if (number < min)
                throw IllegalArgumentException("Expected value >= $min, got $number")
            else
                number
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("Expected float, got \"$arg\"")
        }
    }
}

internal class DoubleArgument(
    val min: Double = -Double.MAX_VALUE,
    val max: Double = Double.MAX_VALUE
) : ArgumentType<Double> {
    override fun parse(reader: StringReader): Double {
        val arg = reader.readArgument()
        return try {
            val number = arg.toDouble()
            if (number > max)
                throw IllegalArgumentException("Expected value <= $max, got $number")
            else if (number < min)
                throw IllegalArgumentException("Expected value >= $min, got $number")
            else
                number
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("Expected double, got \"$arg\"")
        }
    }
}

internal class BigDecimalArgument(
    val min: BigDecimal? = null,
    val max: BigDecimal? = null
) : ArgumentType<BigDecimal> {
    override fun parse(reader: StringReader): BigDecimal {
        val arg = reader.readArgument()
        return try {
            val number = arg.toBigDecimal()
            if (number > max)
                throw IllegalArgumentException("Expected value <= $max, got $number")
            else if (number < min)
                throw IllegalArgumentException("Expected value >= $min, got $number")
            else
                number
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("Expected float, got \"$arg\"")
        }
    }
}

internal object DurationArgument : ArgumentType<Long> {
    private val timePattern = Pattern.compile(
        "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
        Pattern.CASE_INSENSITIVE)

    override fun parse(reader: StringReader): Long {
        val sb = StringBuilder()
        while (reader.canRead() && timePattern.matcher(reader.peekArgument()).matches()) {
            sb.append(reader.readArgument())
        }

        val matcher = timePattern.matcher(sb.toString().trim())

        val years = matcher.group(1)?.toLongOrNull() ?: 0
        val months = matcher.group(2)?.toLongOrNull() ?: 0
        val weeks = matcher.group(3)?.toLongOrNull() ?: 0
        val days = matcher.group(4)?.toLongOrNull() ?: 0
        val hours = matcher.group(5)?.toLongOrNull() ?: 0
        val minutes = matcher.group(6)?.toLongOrNull() ?: 0
        val seconds = matcher.group(7)?.toLongOrNull() ?: 0

        return years * 31_536_000 +
                months * 2_419_200 +
                weeks * 604800 +
                days * 86400 +
                hours * 3600 +
                minutes * 60 +
                seconds
    }
}

internal object OfflineUserArgument : ArgumentType<OfflineUser> {
    override fun parse(reader: StringReader): OfflineUser {
        val arg = reader.readArgument()
        return ZCore.getOfflineUser(arg) ?: throw IllegalArgumentException("Offline user \"$arg\" does not exist")
    }
}

internal object UserArgument : ArgumentType<User> {
    override fun parse(reader: StringReader): User {
        val arg = reader.readArgument()
        return ZCore.getUser(arg) ?: throw IllegalArgumentException("Could not find user \"$arg\"")
    }
}

internal fun StringReader.readArgument(): String {
    val start = cursor
    while (canRead() && peek() != ' ') skip()
    return string.substring(start, cursor)
}

internal fun StringReader.peekArgument(): String {
    val start = cursor
    val arg = readArgument()
    cursor = start
    return arg
}