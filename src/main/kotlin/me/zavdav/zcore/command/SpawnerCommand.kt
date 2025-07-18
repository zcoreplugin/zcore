package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.CreatureType

internal val spawnerCommand = command(
    "spawner",
    "Changes the creature type of a mob spawner.",
    "/spawner <creature>",
    "zcore.spawner"
) {
    creatureArgument("type") {
        runs {
            val type: CreatureType by this
            doSpawner(type)
        }
    }
}

private fun CommandContext<CommandSender>.doSpawner(type: CreatureType) {
    val source = requirePlayer()
    source.spawnerType = type
    source.sendMessage(local("command.spawner.click"))
}