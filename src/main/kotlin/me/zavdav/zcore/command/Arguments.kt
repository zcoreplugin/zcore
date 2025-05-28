package me.zavdav.zcore.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import org.bukkit.Bukkit
import java.math.BigDecimal

internal class StringArgument(val type: StringType) : ArgumentType<String> {
    override fun parse(reader: StringReader): String =
        when (type) {
            StringType.SINGLE_WORD -> reader.readArgument()
            StringType.GREEDY_STRING -> reader.readRemaining()
        }
}

internal enum class StringType { SINGLE_WORD, GREEDY_STRING }

internal inline fun <S> ArgumentBuilder<S, *>.stringArgument(
    name: String,
    type: StringType,
    action: RequiredArgumentBuilder<S, String>.() -> Unit
) = argument(name, StringArgument(type), action)

internal object IntArgument : ArgumentType<Int> {
    override fun parse(reader: StringReader): Int =
        reader.readArgument().toIntOrNull() ?: throw TranslatableException("command.syntaxError")
}

internal inline fun <S> ArgumentBuilder<S, *>.intArgument(
    name: String,
    action: RequiredArgumentBuilder<S, Int>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, IntArgument, action)

internal object DoubleArgument : ArgumentType<Double> {
    override fun parse(reader: StringReader): Double =
        reader.readArgument().toDoubleOrNull() ?: throw TranslatableException("command.syntaxError")
}

internal inline fun <S> ArgumentBuilder<S, *>.doubleArgument(
    name: String,
    action: RequiredArgumentBuilder<S, Double>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, DoubleArgument, action)

internal object BigDecimalArgument : ArgumentType<BigDecimal> {
    override fun parse(reader: StringReader): BigDecimal =
        reader.readArgument().toBigDecimalOrNull() ?: throw TranslatableException("command.syntaxError")
}

internal inline fun <S> ArgumentBuilder<S, *>.bigDecimalArgument(
    name: String,
    action: RequiredArgumentBuilder<S, BigDecimal>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, BigDecimalArgument, action)

internal object PlayerArgument : ArgumentType<CorePlayer> {
    override fun parse(reader: StringReader): CorePlayer {
        val name = reader.readArgument()
        return Bukkit.getPlayer(name)?.core() ?: throw TranslatableException("command.playerOfflineOrUnknown")
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.playerArgument(
    name: String,
    action: RequiredArgumentBuilder<S, CorePlayer>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, PlayerArgument, action)

internal object OfflinePlayerArgument : ArgumentType<OfflinePlayer> {
    override fun parse(reader: StringReader): OfflinePlayer {
        val name = reader.readArgument()
        return ZCore.getOfflinePlayer(name) ?: throw TranslatableException("command.playerUnknown")
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.offlinePlayerArgument(
    name: String,
    action: RequiredArgumentBuilder<S, OfflinePlayer>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, OfflinePlayerArgument, action)

internal fun StringReader.readArgument(): String {
    val start = cursor
    while (canRead() && peek() != ' ') skip()
    return string.substring(start, cursor)
}

internal fun StringReader.readRemaining(): String {
    val arg = remaining
    cursor = totalLength
    return arg
}