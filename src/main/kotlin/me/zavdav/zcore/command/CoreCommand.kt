package me.zavdav.zcore.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import me.zavdav.zcore.util.local
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

internal class CoreCommand(
    name: String,
    aliases: Array<String> = emptyArray(),
    description: String,
    permission: String,
    builder: LiteralArgumentBuilder<CommandSender>
) : Command(name, description, "/$name", aliases.toList()) {

    val node: LiteralCommandNode<CommandSender>

    init {
        this.permission = permission
        this.node = builder.build()
    }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(local("command.noPermission"))
            return true
        }
        val parsedArgs = if (args.isEmpty()) "" else " " + args.joinToString(" ").trim()
        CommandDispatcher.execute(name + parsedArgs, sender)
        return true
    }

}