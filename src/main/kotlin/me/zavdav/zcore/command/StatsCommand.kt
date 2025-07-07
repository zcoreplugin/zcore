package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PagedTable
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode

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

    val statistics = listOf(
        local("command.stats.playtime") to formatPlaytime(target.playtime),
        local("command.stats.blocksPlaced") to target.blocksPlaced,
        local("command.stats.blocksBroken") to target.blocksBroken,
        local("command.stats.blocksTraveled") to target.blocksTraveled.setScale(0, RoundingMode.DOWN),
        local("command.stats.damageDealt") to target.damageDealt,
        local("command.stats.damageTaken") to target.damageTaken,
        local("command.stats.playersKilled") to target.playersKilled,
        local("command.stats.mobsKilled") to target.mobsKilled,
        local("command.stats.deaths") to target.deaths
    )

    source.sendMessage(local("command.stats", target.name))
    source.sendMessage(line(ChatColor.GRAY))
    val table = PagedTable(statistics) { _, (key, value) ->
        arrayOf(key to 1, "${ChatColor.GREEN}$value" to 1)
    }

    table.print(0, source)
}

private fun formatPlaytime(playtime: Long): String =
    "${playtime.toBigDecimal().divide(BigDecimal("3600000"), 1, RoundingMode.DOWN)}h"