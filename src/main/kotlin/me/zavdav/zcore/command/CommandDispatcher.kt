package me.zavdav.zcore.command

import com.mojang.brigadier.exceptions.CommandSyntaxException
import org.bukkit.command.CommandSender

internal object CommandDispatcher : com.mojang.brigadier.CommandDispatcher<CommandSender>() {

    override fun execute(input: String, source: CommandSender): Int {
        try {
            return super.execute(input, source)
        } catch (e: CommandSyntaxException) {
            when (e.type) {
                is PlayerNotOnlineExceptionType -> throw TranslatableException("command.playerNotOnline")
                is AmbiguousNameExceptionType -> throw TranslatableException("command.ambiguousName")
                is PlayerUnknownExceptionType -> throw TranslatableException("command.playerUnknown")
                is MaterialUnknownExceptionType -> throw TranslatableException("command.materialUnknown")
                else -> throw TranslatableException("command.syntaxError")
            }
        } catch (e: TranslatableException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            throw TranslatableException("command.genericError")
        }
    }

}