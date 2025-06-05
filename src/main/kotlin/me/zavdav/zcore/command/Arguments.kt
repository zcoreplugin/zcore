package me.zavdav.zcore.command

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import java.math.BigDecimal

internal object PlayerNotOnlineExceptionType : CommandExceptionType {
    fun create() = CommandSyntaxException(this, LiteralMessage(tl("command.playerNotOnline")))
}

internal object PlayerUnknownExceptionType : CommandExceptionType {
    fun create() = CommandSyntaxException(this, LiteralMessage(tl("command.playerUnknown")))
}

private val PLAYER_NOT_ONLINE = PlayerNotOnlineExceptionType
private val PLAYER_UNKNOWN = PlayerUnknownExceptionType

internal object StringArgument : ArgumentType<String> {
    override fun parse(reader: StringReader): String = reader.readArgument()
}

internal inline fun <S> ArgumentBuilder<S, *>.stringArgument(
    name: String,
    action: RequiredArgumentBuilder<S, String>.() -> Unit
) = argument(name, StringArgument, action)

internal object TextArgument : ArgumentType<String> {
    override fun parse(reader: StringReader): String = reader.readRemaining()
}

internal inline fun <S> ArgumentBuilder<S, *>.textArgument(
    name: String,
    action: RequiredArgumentBuilder<S, String>.() -> Unit
) = argument(name, TextArgument, action)

internal object IntArgument : ArgumentType<Int> {
    override fun parse(reader: StringReader): Int = reader.readArgument().toInt()
}

internal inline fun <S> ArgumentBuilder<S, *>.intArgument(
    name: String,
    action: RequiredArgumentBuilder<S, Int>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, IntArgument, action)

internal object DoubleArgument : ArgumentType<Double> {
    override fun parse(reader: StringReader): Double = reader.readArgument().toDouble()
}

internal inline fun <S> ArgumentBuilder<S, *>.doubleArgument(
    name: String,
    action: RequiredArgumentBuilder<S, Double>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, DoubleArgument, action)

internal object BigDecimalArgument : ArgumentType<BigDecimal> {
    override fun parse(reader: StringReader): BigDecimal = reader.readArgument().toBigDecimal()
}

internal inline fun <S> ArgumentBuilder<S, *>.bigDecimalArgument(
    name: String,
    action: RequiredArgumentBuilder<S, BigDecimal>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, BigDecimalArgument, action)

internal object PlayerArgument : ArgumentType<CorePlayer> {
    override fun parse(reader: StringReader): CorePlayer =
        Bukkit.getPlayer(reader.readArgument())?.core() ?: throw PLAYER_NOT_ONLINE.create()
}

internal inline fun <S> ArgumentBuilder<S, *>.playerArgument(
    name: String,
    action: RequiredArgumentBuilder<S, CorePlayer>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, PlayerArgument, action)

internal object OfflinePlayerArgument : ArgumentType<OfflinePlayer> {
    override fun parse(reader: StringReader): OfflinePlayer =
        ZCore.getOfflinePlayer(reader.readArgument()) ?: throw PLAYER_UNKNOWN.create()
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