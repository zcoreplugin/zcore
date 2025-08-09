package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.statistic.Statistic
import me.zavdav.zcore.util.alignText
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val statsCommand = command(
    "stats",
    arrayOf("statistics"),
    "Shows your statistics.",
    "/stats",
    "zcore.stats"
) {
    runs {
        val source = requirePlayer()
        doStats(source.data)
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doStats(target)
        }
    }
}

private fun CommandContext<CommandSender>.doStats(target: OfflinePlayer) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.stats.other")

    source.sendMessage(local("command.stats", target.name))
    source.sendMessage(line(ChatColor.GRAY))
    Statistic.getAllRegistered().forEach {
        source.sendMessage(alignText(
            local("command.stats.name", it.name) to 1,
            local("command.stats.score", it.getFormattedScore(target)) to 1
        ))
    }
}