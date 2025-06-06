package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PageBuilder
import me.zavdav.zcore.util.tl
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
        tl("command.stats.playtime") to formatPlaytime(target.playtime),
        tl("command.stats.blocksPlaced") to target.blocksPlaced,
        tl("command.stats.blocksBroken") to target.blocksBroken,
        tl("command.stats.blocksTraveled") to target.blocksTraveled.setScale(0, RoundingMode.FLOOR),
        tl("command.stats.damageDealt") to "${target.damageDealt} HP",
        tl("command.stats.damageTaken") to "${target.damageTaken} HP",
        tl("command.stats.playersKilled") to target.playersKilled,
        tl("command.stats.mobsKilled") to target.mobsKilled,
        tl("command.stats.deaths") to target.deaths
    )

    val builder = PageBuilder {
        header(tl("command.stats.header"))
    }

    statistics.forEach { (statistic, value) ->
        builder.row {
            cell(1, statistic)
            cell(1, value)
        }
    }

    val page = builder.create()
    page.print(source)
}

private fun formatPlaytime(playtime: Long): String {
    val hours = playtime.toBigDecimal()
        .divide(BigDecimal("3600000"), 1, RoundingMode.FLOOR)
    return "$hours h"
}