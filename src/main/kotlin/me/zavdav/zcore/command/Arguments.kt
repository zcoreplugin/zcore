package me.zavdav.zcore.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.user.CorePlayer
import me.zavdav.zcore.user.OfflineUser
import me.zavdav.zcore.user.core
import org.bukkit.Bukkit
import java.math.BigDecimal
import java.util.regex.Pattern

internal class StringArgument(
    val type: StringType,
    val maxLength: Int = Int.MAX_VALUE
) : ArgumentType<String> {
    override fun parse(reader: StringReader): String {
        val string = when (type) {
            StringType.SINGLE_WORD -> reader.readArgument()
            StringType.GREEDY_STRING -> reader.readRemaining()
        }
        return if (string.length <= maxLength)
            string
        else
            throw IllegalArgumentException("Expected max length $maxLength, got length $string.length")
    }
}

internal enum class StringType { SINGLE_WORD, GREEDY_STRING }

internal inline fun <S> ArgumentBuilder<S, *>.stringArgument(
    name: String,
    type: StringType,
    maxLength: Int = Int.MAX_VALUE,
    action: RequiredArgumentBuilder<S, String>.() -> Unit
) = argument(name, StringArgument(type, maxLength), action)

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

internal inline fun <S> ArgumentBuilder<S, *>.intArgument(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    action: RequiredArgumentBuilder<S, Int>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, IntArgument(min, max), action)

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

internal inline fun <S> ArgumentBuilder<S, *>.longArgument(
    name: String,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
    action: RequiredArgumentBuilder<S, Long>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, LongArgument(min, max), action)

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

internal inline fun <S> ArgumentBuilder<S, *>.floatArgument(
    name: String,
    min: Float = -Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE,
    action: RequiredArgumentBuilder<S, Float>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, FloatArgument(min, max), action)

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

internal inline fun <S> ArgumentBuilder<S, *>.doubleArgument(
    name: String,
    min: Double = -Double.MAX_VALUE,
    max: Double = Double.MAX_VALUE,
    action: RequiredArgumentBuilder<S, Double>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, DoubleArgument(min, max), action)

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

internal inline fun <S> ArgumentBuilder<S, *>.bigDecimalArgument(
    name: String,
    min: BigDecimal? = null,
    max: BigDecimal? = null,
    action: RequiredArgumentBuilder<S, BigDecimal>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, BigDecimalArgument(min, max), action)

internal object DurationArgument : ArgumentType<Long> {
    private val timePattern = Pattern.compile(
        "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" +
        "(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
        Pattern.CASE_INSENSITIVE
    )

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

internal inline fun <S> ArgumentBuilder<S, *>.durationArgument(
    name: String,
    action: RequiredArgumentBuilder<S, Long>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, DurationArgument, action)

internal object PlayerArgument : ArgumentType<CorePlayer> {
    override fun parse(reader: StringReader): CorePlayer {
        val arg = reader.readArgument()
        return Bukkit.getPlayer(arg)?.core() ?: throw IllegalArgumentException("Could not find user \"$arg\"")
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.playerArgument(
    name: String,
    action: RequiredArgumentBuilder<S, CorePlayer>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, PlayerArgument, action)

internal object OfflineUserArgument : ArgumentType<OfflineUser> {
    override fun parse(reader: StringReader): OfflineUser {
        val arg = reader.readArgument()
        return ZCore.getOfflineUser(arg) ?: throw IllegalArgumentException("Offline user \"$arg\" does not exist")
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.offlineUserArgument(
    name: String,
    action: RequiredArgumentBuilder<S, OfflineUser>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, OfflineUserArgument, action)

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

internal fun StringReader.readRemaining(): String {
    val arg = remaining
    cursor = totalLength
    return arg
}