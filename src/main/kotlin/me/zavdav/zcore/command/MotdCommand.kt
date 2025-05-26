package me.zavdav.zcore.command

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.fmt
import org.bukkit.Bukkit
import org.bukkit.entity.Player

internal val motdCommand = command(
    "motd",
    "Shows the message of the day.",
    "/motd",
    "zcore.motd"
) {
    runs(permission) {
        val source = requirePlayer()
        createMotd(source).forEach { source.sendMessage(it) }
    }
}

private fun createMotd(player: Player): List<String> {
    val lines = Config.motd.toMutableList()
    for (i in lines.indices) {
        lines[i] = fmt(
            lines[i],
            "name" to player.name,
            "displayname" to player.displayName,
            "playercount" to Bukkit.getOnlinePlayers().size,
            "maxplayers" to Bukkit.getMaxPlayers()
        )
    }
    return lines
}