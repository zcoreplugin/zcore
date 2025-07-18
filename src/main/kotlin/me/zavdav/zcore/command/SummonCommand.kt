package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.displayName
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.CreatureType

internal val summonCommand = command(
    "summon",
    arrayOf("spawnmob"),
    "Spawns a creature at your location.",
    "/summon <creature>",
    "zcore.summon"
) {
    creatureArgument("type") {
        runs {
            val type: CreatureType by this
            doSummon(type)
        }
    }
}

private fun CommandContext<CommandSender>.doSummon(type: CreatureType) {
    val source = requirePlayer()
    source.world.spawnCreature(source.location, type)
    source.sendMessage(local("command.summon", type.displayName))
}