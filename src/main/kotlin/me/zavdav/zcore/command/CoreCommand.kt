package me.zavdav.zcore.command

import me.zavdav.zcore.util.local
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

internal class CoreCommand(
    name: String,
    aliases: Array<String> = emptyArray(),
    description: String,
    usage: String,
    permission: String,
    val builder: CommandBuilder
) : Command(name, description, usage, aliases.toList()) {

    init { this.permission = permission }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (!sender.isOp && !sender.hasPermission(permission)) {
            sender.sendMessage(local("command.noPermission"))
            return true
        }
        val parsedArgs = if (args.isEmpty()) "" else " " + args.joinToString(" ").trim()
        CommandDispatcher.execute(name + parsedArgs, sender)
        return true
    }

}