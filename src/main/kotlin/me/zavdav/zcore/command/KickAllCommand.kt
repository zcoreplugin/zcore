package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val kickallCommand = command(
    "kickall",
    "Kicks all players",
    "zcore.kickall"
) {
    runs {
        doKickall(ZCoreConfig.getString("command.kick.default-reason"))
    }
    textArgument("reason") {
        runs {
            val reason: String by this
            doKickall(reason)
        }
    }
}

private fun CommandContext<CommandSender>.doKickall(reason: String) {
    val source = this.source
    for (player in Bukkit.getOnlinePlayers()) {
        if (source is Player && player.uniqueId == source.uniqueId)
            continue

        player.kickPlayer(local("command.kick.message", reason))
    }

    source.sendMessage(local("command.kickall", reason))
}