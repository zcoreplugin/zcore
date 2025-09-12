package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val setspawnCommand = command(
    "setspawn",
    "Sets the spawn point",
    "zcore.setspawn"
) {
    runs {
        doSetSpawn()
    }
}

private fun CommandContext<CommandSender>.doSetSpawn() {
    val source = requirePlayer()
    val world = Bukkit.getWorlds()[0]
    val location = source.location

    world.setSpawnLocation(location.blockX, location.blockY, location.blockZ)
    source.sendMessage(local("command.setspawn", location.blockX, location.blockY, location.blockZ))
}