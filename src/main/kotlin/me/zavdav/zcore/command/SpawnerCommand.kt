package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.CreatureType

internal val spawnerCommand = command(
    "spawner",
    "Changes the mob type of a spawner",
    "zcore.spawner"
) {
    creatureArgument("mobType") {
        runs {
            val mobType: CreatureType by this
            doSpawner(mobType)
        }
    }
}

private fun CommandContext<CommandSender>.doSpawner(type: CreatureType) {
    val source = requirePlayer()
    source.spawnerType = type
    source.sendMessage(local("command.spawner.click"))
}