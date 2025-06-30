package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val spawnCommand = command(
    "spawn",
    "Teleports you to the spawn point.",
    "/spawn",
    "zcore.spawn"
) {
    runs {
        doSpawn()
    }
}

private fun CommandContext<CommandSender>.doSpawn() {
    val source = requirePlayer()
    val world = Bukkit.getWorlds()[0]
    if (!source.safelyTeleport(world.spawnLocation))
        throw TranslatableException("command.spawn.unsafeLocation")

    source.sendMessage(tl("command.spawn"))
}