package me.zavdav.zcore.util

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import me.zavdav.zcore.data.user.User

internal val commandDispatcher = CommandDispatcher<User>()

internal class Argument<T>(val name: String, val type: Class<T>)

internal inline fun command(
    literal: String,
    action: LiteralArgumentBuilder<User>.() -> Unit
): LiteralCommandNode<User> =
    commandDispatcher.register(
        LiteralArgumentBuilder.literal<User>(literal).apply(action)
    )

internal inline fun <S> ArgumentBuilder<S, *>.literal(
    literal: String,
    action: LiteralArgumentBuilder<S>.() -> Unit
): ArgumentBuilder<S, *> {
    then(LiteralArgumentBuilder.literal<S>(literal).apply(action))
    return this
}

internal inline fun <S, reified T> ArgumentBuilder<S, *>.argument(
    name: String,
    type: ArgumentType<T>,
    action: RequiredArgumentBuilder<S, T>.(Argument<T>) -> Unit
): ArgumentBuilder<S, *> {
    val argument = Argument(name, T::class.java)
    then(RequiredArgumentBuilder.argument<S, T>(name, type).apply { action(argument) })
    return this
}

internal fun <S> ArgumentBuilder<S, *>.runs(
    action: CommandContext<S>.() -> Unit
): ArgumentBuilder<S, *> {
    executes {
        action(it)
        return@executes 0
    }
    return this
}

internal fun <S, T> CommandContext<S>.get(argument: Argument<T>): T =
    getArgument(argument.name, argument.type)