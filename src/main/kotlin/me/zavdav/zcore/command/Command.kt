package me.zavdav.zcore.command

import com.mojang.brigadier.tree.LiteralCommandNode
import me.zavdav.zcore.ZCore
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.SimplePluginManager

internal class Command(
    val name: String,
    val aliases: Array<String> = emptyArray(),
    val description: String,
    val usage: String,
    val permission: String,
    builder: CommandBuilder
) {

    val node: LiteralCommandNode<CommandSender> = commandDispatcher.register(builder)

    private val bukkitCommand: PluginCommand by lazy {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        val pluginCommand = constructor.newInstance(name, ZCore.INSTANCE)
        constructor.isAccessible = false

        pluginCommand.also {
            it.aliases = aliases.toList()
            it.description = description
            it.usage = usage
            it.executor = ZCore.INSTANCE
        }
    }

    fun register() {
        bukkitCommand.register(commandMap)
    }

    fun unregister() {
        bukkitCommand.unregister(commandMap)
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