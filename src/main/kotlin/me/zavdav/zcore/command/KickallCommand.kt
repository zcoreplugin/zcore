package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val kickallCommand = command(
    "kickall",
    "Kicks all players.",
    "/kickall [<reason>]",
    "zcore.kickall"
) {
    runs {
        doKickall(Config.defaultKickReason)
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

        player.kickPlayer(tl("command.kick.message", reason))
    }

    source.sendMessage(tl("command.kickall", reason))
}