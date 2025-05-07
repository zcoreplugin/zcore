package me.zavdav.zcore.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.reflect.KProperty

internal inline fun <S> command(
    literal: String,
    action: LiteralArgumentBuilder<S>.() -> Unit
): LiteralArgumentBuilder<S> =
    LiteralArgumentBuilder.literal<S>(literal).apply(action)

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

internal fun <S> ArgumentBuilder<S, *>.runs(
    action: CommandContext<S>.() -> Unit
): ArgumentBuilder<S, *> {
    executes {
        action(it)
        return@executes 1
    }
    return this
}

internal inline operator fun <reified V> CommandContext<*>.getValue(
    thisRef: Any?,
    property: KProperty<*>
): V = getArgument(property.name, V::class.java)