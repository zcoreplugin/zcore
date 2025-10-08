package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val helpCommand = command(
    "help",
    "Shows available commands and command help",
    "zcore.help"
) {
    runs {
        doHelp(1)
    }
    intArgument("page") {
        runs {
            val page: Int by this
            doHelp(page)
        }
    }
    stringArgument("command") {
        runs {
            val command: String by this
            doHelpCommand(command)
        }
    }
}

private fun CommandContext<CommandSender>.doHelp(page: Int) {
    val commands = CommandDispatcher.getCommands()
        .filter { if (it.permission == null) source.isOp
                  else source.hasPermission(it.permission) }

    val list = PagingList(commands, 10)
    if (list.isEmpty()) return

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.help", index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.help.line", it.name, it.description))
    }
}

private fun CommandContext<CommandSender>.doHelpCommand(name: String) {
    val command = CommandDispatcher.getCommand(name)
    if (command == null)
        throw TranslatableException("command.help.unknown", name)

    val hasPermission = if (command.permission == null) source.isOp
                        else source.hasPermission(command.permission)
    if (!hasPermission)
        throw TranslatableException("command.help.unknown", name)

    source.sendMessage(local("command.help.command", command.name))
    source.sendMessage(line(ChatColor.GRAY))

    source.sendMessage(local("command.help.description"))
    source.sendMessage(local("command.help.description.value", command.description))

    val syntaxList = mutableListOf<String>()
    if (command is CoreCommand) {
        val allUsage = CommandDispatcher.getAllUsage(command.node, source, true)
        syntaxList.addAll(allUsage.map { "/${command.name} $it" })
    } else {
        val allUsage = command.usage.replace("/<command>".toRegex(), "/${command.name}").split("\n")
        syntaxList.addAll(allUsage)
    }

    source.sendMessage(local("command.help.syntax"))
    syntaxList.forEach { source.sendMessage(local("command.help.syntax.value", it)) }

    if (command.aliases.isNotEmpty()) {
        val aliases = command.aliases.joinToString(", ") { "/$it" }
        source.sendMessage(local("command.help.aliases"))
        source.sendMessage(local("command.help.aliases.value", aliases))
    }
}