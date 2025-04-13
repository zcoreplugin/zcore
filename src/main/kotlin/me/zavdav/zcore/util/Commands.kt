package me.zavdav.zcore.util

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.tree.LiteralCommandNode
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.exception.UserNotFoundException
import me.zavdav.zcore.user.OfflineUser
import me.zavdav.zcore.user.User
import java.math.BigDecimal

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

internal fun StringReader.readWord(): String {
    val start = cursor
    while (canRead() && peek() != ' ') skip()
    return string.substring(start, cursor)
}

internal fun StringReader.readBigDecimal(): BigDecimal {
    val start = cursor
    while (canRead() && StringReader.isAllowedNumber(peek())) skip()
    val number = string.substring(start, cursor)

    return try {
        BigDecimal(number)
    } catch (_: NumberFormatException) {
        val message = LiteralMessage("Expected BigDecimal")
        throw CommandSyntaxException(SimpleCommandExceptionType(message), message)
    }
}

internal fun string() = StringArgumentType.string()

internal fun word() = WordArgumentType

internal fun greedyString() = StringArgumentType.greedyString()

internal fun boolean() = BoolArgumentType.bool()

internal fun int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) =
    IntegerArgumentType.integer(min, max)

internal fun long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) =
    LongArgumentType.longArg(min, max)

internal fun float(min: Float = -Float.MAX_VALUE, max: Float = Float.MAX_VALUE) =
    FloatArgumentType.floatArg(min, max)

internal fun double(min: Double = -Double.MAX_VALUE, max: Double = Double.MAX_VALUE) =
    DoubleArgumentType.doubleArg(min, max)

internal fun bigDecimal(min: BigDecimal = BigDecimal.ZERO) = BigDecimalArgumentType(min)

internal fun user() = UserArgumentType

internal fun offlineUser() = OfflineUserArgumentType

internal object WordArgumentType : ArgumentType<String> {
    override fun parse(reader: StringReader): String =
        reader.readWord()
}

internal class BigDecimalArgumentType(val min: BigDecimal) : ArgumentType<BigDecimal> {
    override fun parse(reader: StringReader): BigDecimal {
        val start = reader.cursor
        val result = reader.readBigDecimal()

        if (result < min) {
            reader.cursor = start
            val message = LiteralMessage("BigDecimal must be at least $min")
            throw CommandSyntaxException(SimpleCommandExceptionType(message), message)
        }

        return result
    }
}

internal object UserArgumentType : ArgumentType<User> {
    override fun parse(reader: StringReader): User {
        val name = reader.readWord()
        return ZCore.getUser(name) ?: throw UserNotFoundException(name)
    }
}

internal object OfflineUserArgumentType : ArgumentType<OfflineUser> {
    override fun parse(reader: StringReader): OfflineUser {
        val name = reader.readWord()
        return ZCore.getOfflineUser(name) ?: throw UserNotFoundException(name)
    }
}