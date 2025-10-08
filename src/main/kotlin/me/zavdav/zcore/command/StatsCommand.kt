package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.statistic.Statistic
import me.zavdav.zcore.util.alignText
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val statsCommand = command(
    "stats",
    arrayOf("statistics"),
    "Shows a player's statistics",
    "zcore.stats"
) {
    runs {
        val source = requirePlayer()
        doStats(source.data)
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.stats.other")
        runs {
            val player: OfflinePlayer by this
            doStats(player)
        }
    }
}

private fun CommandContext<CommandSender>.doStats(target: OfflinePlayer) {
    source.sendMessage(local("command.stats", target.name))
    source.sendMessage(line(ChatColor.GRAY))
    Statistic.getAllRegistered().forEach {
        source.sendMessage(alignText(
            local("command.stats.name", it.name) to 1,
            local("command.stats.score", it.getFormattedScore(target)) to 1
        ))
    }
}