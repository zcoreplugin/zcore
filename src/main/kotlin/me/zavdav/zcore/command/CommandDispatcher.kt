package me.zavdav.zcore.command

import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal object CommandDispatcher : com.mojang.brigadier.CommandDispatcher<CommandSender>() {

    override fun execute(input: String, source: CommandSender): Int {
        try {
            return super.execute(input, source)
        } catch (e: CommandSyntaxException) {
            when (e.type) {
                is NameNoMatchesExceptionType,
                is NameMultipleMatchesExceptionType,
                is UnknownPlayerExceptionType,
                is UnknownMaterialExceptionType -> source.sendMessage(e.rawMessage.string)
                else -> source.sendMessage(local("command.syntaxError"))
            }
        } catch (e: TranslatableException) {
            source.sendMessage(local(e.key, *e.args))
        } catch (e: Throwable) {
            e.printStackTrace()
            source.sendMessage(local("command.genericError"))
        }

        return 0
    }

}