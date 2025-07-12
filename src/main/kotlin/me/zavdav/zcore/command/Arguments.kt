package me.zavdav.zcore.command

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.IpAddressRange
import me.zavdav.zcore.util.DURATION_PATTERN
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.Materials
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.parseDuration
import org.bukkit.Material
import java.math.BigDecimal

internal object NameNoMatchesExceptionType : DynamicCommandExceptionType({
    input -> LiteralMessage(local("command.nameNoMatches", input))
})

internal object NameMultipleMatchesExceptionType : DynamicCommandExceptionType({
    input -> LiteralMessage(local("command.nameMultipleMatches", input))
})

internal object UnknownPlayerExceptionType : DynamicCommandExceptionType({
    input -> LiteralMessage(local("command.unknownPlayer", input))
})

internal object UnknownMaterialExceptionType : DynamicCommandExceptionType({
    input -> LiteralMessage(local("command.unknownMaterial", input))
})

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

internal object BigDecimalArgument : ArgumentType<BigDecimal> {
    override fun parse(reader: StringReader): BigDecimal = reader.readArgument().toBigDecimal()
}

internal inline fun <S> ArgumentBuilder<S, *>.bigDecimalArgument(
    name: String,
    action: RequiredArgumentBuilder<S, BigDecimal>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, BigDecimalArgument, action)

internal object DurationArgument : ArgumentType<Long> {
    override fun parse(reader: StringReader): Long {
        val matcher = DURATION_PATTERN.matcher(reader.remaining)
        var end = 0

        while (matcher.find() && matcher.start() == end)
            end = matcher.end()

        val duration = reader.remaining.substring(0, end).trimEnd()
        reader.cursor += duration.length
        return parseDuration(duration)!!
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.durationArgument(
    name: String,
    action: RequiredArgumentBuilder<S, Long>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, DurationArgument, action)

internal object IpAddressRangeArgument : ArgumentType<IpAddressRange> {
    override fun parse(reader: StringReader): IpAddressRange = IpAddressRange.parse(reader.readArgument())
}

internal inline fun <S> ArgumentBuilder<S, *>.ipAddressRangeArgument(
    name: String,
    action: RequiredArgumentBuilder<S, IpAddressRange>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, IpAddressRangeArgument, action)

internal object PlayerArgument : ArgumentType<CorePlayer> {
    override fun parse(reader: StringReader): CorePlayer {
        val name = reader.readArgument()
        val matches = ZCore.matchPlayer(name)
        return when (matches.size) {
            0 -> throw NameNoMatchesExceptionType.create(name)
            1 -> matches.first()
            else -> throw NameMultipleMatchesExceptionType.create(name)
        }
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.playerArgument(
    name: String,
    action: RequiredArgumentBuilder<S, CorePlayer>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, PlayerArgument, action)

internal object OfflinePlayerArgument : ArgumentType<OfflinePlayer> {
    override fun parse(reader: StringReader): OfflinePlayer {
        val name = reader.readArgument()
        return ZCore.getOfflinePlayer(name) ?: throw UnknownPlayerExceptionType.create(name)
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.offlinePlayerArgument(
    name: String,
    action: RequiredArgumentBuilder<S, OfflinePlayer>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, OfflinePlayerArgument, action)

internal object MaterialArgument : ArgumentType<MaterialData> {
    override fun parse(reader: StringReader): MaterialData {
        val string = reader.readArgument()
        var material = Materials.getByName(string)

        try {
            if (material == null) {
                val components = string.split(":", limit = 2)
                val type = Material.getMaterial(components[0].toInt().coerceAtLeast(1))!!
                val data = components.getOrNull(1)?.toShort()?.coerceAtLeast(0) ?: 0
                material = MaterialData(type, data)
            }
        } catch (_: Exception) {
            throw UnknownMaterialExceptionType.create(string)
        }

        return material
    }
}

internal inline fun <S> ArgumentBuilder<S, *>.materialArgument(
    name: String,
    action: RequiredArgumentBuilder<S, MaterialData>.() -> Unit
): ArgumentBuilder<S, *> = argument(name, MaterialArgument, action)

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