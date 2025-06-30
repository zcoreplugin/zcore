package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.util.fmt
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val motdCommand = command(
    "motd",
    "Shows the message of the day.",
    "/motd",
    "zcore.motd"
) {
    runs {
        doMotd()
    }
}

private fun CommandContext<CommandSender>.doMotd() {
    val source = requirePlayer()
    val lines = ZCoreConfig.getStringList("command.motd.lines").toMutableList()

    for (i in lines.indices) {
        lines[i] = fmt(
            lines[i],
            "name" to source.name,
            "displayname" to source.displayName,
            "playercount" to Bukkit.getOnlinePlayers().size,
            "maxplayers" to Bukkit.getMaxPlayers()
        )
    }

    lines.forEach { source.sendMessage(it) }
}