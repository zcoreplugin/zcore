package me.zavdav.zcore.command

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.plugin.SimplePluginManager

internal class CoreCommand(
    name: String,
    aliases: Array<String> = emptyArray(),
    description: String,
    usage: String,
    permission: String,
    val builder: CommandBuilder
) : Command(name, description, usage, aliases.toList()) {

    init { this.permission = permission }

    fun register() {
        commandMap.register(ZCore.INSTANCE.description.name, this)
        CommandDispatcher.register(builder)
    }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (!sender.isOp && !sender.hasPermission(permission)) {
            sender.sendMessage(local("command.noPermission"))
            return true
        }
        val parsedArgs = if (args.isEmpty()) "" else " " + args.joinToString(" ").trim()
        CommandDispatcher.execute(name + parsedArgs, sender)
        return true
    }

    internal companion object {
        private val commandMap: CommandMap by lazy {
            val field = SimplePluginManager::class.java.getDeclaredField("commandMap")
            field.isAccessible = true
            val commandMap = field.get(Bukkit.getPluginManager()) as CommandMap
            field.isAccessible = false
            commandMap
        }
    }

}