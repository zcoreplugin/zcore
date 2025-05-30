package me.zavdav.zcore.command

import com.mojang.brigadier.exceptions.CommandSyntaxException
import org.bukkit.command.CommandSender

internal object CommandDispatcher : com.mojang.brigadier.CommandDispatcher<CommandSender>() {

    override fun execute(input: String, source: CommandSender): Int {
        try {
            return super.execute(input, source)
        } catch (_: CommandSyntaxException) {
            throw TranslatableException("command.syntaxError")
        } catch (e: TranslatableException) {
            throw e
        } catch (_: Throwable) {
            throw TranslatableException("command.genericError")
        }
    }

}