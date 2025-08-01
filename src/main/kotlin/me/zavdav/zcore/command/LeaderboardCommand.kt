package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.PagedTable
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import java.math.RoundingMode

internal val leaderboardCommand = command(
    "leaderboard",
    arrayOf("lbd"),
    "Shows the leaderboard for a statistic.",
    "/leaderboard <statistic> [<page>]",
    "zcore.leaderboard"
) {
    literal("playtime") {
        runs {
            doLeaderboardPlaytime(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardPlaytime(page)
            }
        }
    }
    literal("blocksplaced") {
        runs {
            doLeaderboardBlocksPlaced(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardBlocksPlaced(page)
            }
        }
    }
    literal("blocksbroken") {
        runs {
            doLeaderboardBlocksBroken(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardBlocksBroken(page)
            }
        }
    }
    literal("blockstraveled") {
        runs {
            doLeaderboardBlocksTraveled(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardBlocksTraveled(page)
            }
        }
    }
    literal("damagedealt") {
        runs {
            doLeaderboardDamageDealt(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardDamageDealt(page)
            }
        }
    }
    literal("damagetaken") {
        runs {
            doLeaderboardDamageTaken(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardDamageTaken(page)
            }
        }
    }
    literal("playerskilled") {
        runs {
            doLeaderboardPlayersKilled(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardPlayersKilled(page)
            }
        }
    }
    literal("mobskilled") {
        runs {
            doLeaderboardMobsKilled(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardMobsKilled(page)
            }
        }
    }
    literal("deaths") {
        runs {
            doLeaderboardDeaths(1)
        }
        intArgument("page") {
            runs {
                val page: Int by this
                doLeaderboardDeaths(page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doLeaderboardPlaytime(page: Int) {
    printLeaderboard(
        "command.leaderboard.playtime", page,
        { it.playtime }, { formatPlaytime(it) }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardBlocksPlaced(page: Int) {
    printLeaderboard(
        "command.leaderboard.blocksPlaced", page,
        { it.blocksPlaced }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardBlocksBroken(page: Int) {
    printLeaderboard(
        "command.leaderboard.blocksBroken", page,
        { it.blocksBroken }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardBlocksTraveled(page: Int) {
    printLeaderboard(
        "command.leaderboard.blocksTraveled", page,
        { it.blocksTraveled }, { it.setScale(0, RoundingMode.DOWN) }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardDamageDealt(page: Int) {
    printLeaderboard(
        "command.leaderboard.damageDealt", page,
        { it.damageDealt }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardDamageTaken(page: Int) {
    printLeaderboard(
        "command.leaderboard.damageTaken", page,
        { it.damageTaken }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardPlayersKilled(page: Int) {
    printLeaderboard(
        "command.leaderboard.playersKilled", page,
        { it.playersKilled }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardMobsKilled(page: Int) {
    printLeaderboard(
        "command.leaderboard.mobsKilled", page,
        { it.mobsKilled }
    )
}

private fun CommandContext<CommandSender>.doLeaderboardDeaths(page: Int) {
    printLeaderboard(
        "command.leaderboard.deaths", page,
        { it.deaths }
    )
}

private fun formatPlaytime(playtime: Long): String =
    "${playtime.toBigDecimal().divide(BigDecimal("3600000"), 1, RoundingMode.DOWN)}h"

private fun <V : Comparable<V>> CommandContext<CommandSender>.printLeaderboard(
    langKey: String,
    page: Int,
    selector: (OfflinePlayer) -> V,
    transform: (V) -> Any = { it -> it }
) {
    val source = this.source
    val players = ZCore.players.sortedByDescending(selector)
    val leaderboard = PagedTable(players, 10) { i, player -> arrayOf(
        local("command.leaderboard.rank", i + 1, player.name) to 1,
        local("command.leaderboard.amount", transform(selector(player))) to 1
    ) }

    if (leaderboard.pages() == 0) return
    val pageNumber = page.coerceIn(1..leaderboard.pages())

    source.sendMessage(local(langKey, pageNumber, leaderboard.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    leaderboard.print(pageNumber - 1, source)
}