package me.zavdav.zcore.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

internal inline fun command(
    name: String,
    aliases: Array<String>,
    description: String,
    permission: String,
    action: LiteralArgumentBuilder<CommandSender>.() -> Unit
): CoreCommand {
    val builder = LiteralArgumentBuilder.literal<CommandSender>(name).apply { action() }
    return CoreCommand(name, aliases, description, permission, builder)
}

internal inline fun command(
    name: String,
    description: String,
    permission: String,
    action: LiteralArgumentBuilder<CommandSender>.() -> Unit
): CoreCommand = command(name, emptyArray(), description, permission, action)

internal inline fun <S> ArgumentBuilder<S, *>.literal(
    literal: String,
    action: LiteralArgumentBuilder<S>.() -> Unit
): ArgumentBuilder<S, *> {
    then(LiteralArgumentBuilder.literal<S>(literal).apply(action))
    return this
}

internal inline fun <S, T> ArgumentBuilder<S, *>.argument(
    name: String,
    type: ArgumentType<T>,
    action: RequiredArgumentBuilder<S, T>.() -> Unit
): ArgumentBuilder<S, *> {
    then(RequiredArgumentBuilder.argument<S, T>(name, type).apply { action() })
    return this
}

internal fun <S : CommandSender> ArgumentBuilder<S, *>.runs(
    action: CommandContext<S>.() -> Unit
): ArgumentBuilder<S, *> {
    executes {
        action(it)
        return@executes 1
    }
    return this
}

internal fun <S : CommandSender> ArgumentBuilder<S, *>.requiresPermission(
    permission: String
): ArgumentBuilder<S, *> {
    requires { it.hasPermission(permission) }
    return this
}

internal fun <S : CommandSender> CommandContext<S>.requirePlayer(): CorePlayer {
    val source = this.source
    if (source !is Player) throw TranslatableException("command.playerRequired")
    return source.core()
}

internal inline operator fun <reified V> CommandContext<*>.getValue(
    thisRef: Any?,
    property: KProperty<*>
): V = getArgument(property.name, V::class.java)