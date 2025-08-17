package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.statistic.Statistic
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.alignText
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val leaderboardCommand = command(
    "leaderboard",
    arrayOf("lbd"),
    "Shows the leaderboard for a statistic",
    "zcore.leaderboard"
) {
    stringArgument("category") {
        runs {
            val category: String by this
            doLeaderboard(category, 1)
        }
        intArgument("page") {
            runs {
                val category: String by this
                val page: Int by this
                doLeaderboard(category, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doLeaderboard(category: String, page: Int) {
    val statistic = Statistic.getByName(category)
    if (statistic == null)
        throw TranslatableException("command.leaderboard.unknown", category)

    val players = ZCore.players.sortedWith(
        compareByDescending<OfflinePlayer> { statistic.getScore(it) }
            .then { p1, p2 -> p1.name.compareTo(p2.name, true) })
    val list = PagingList(players, 10)
    if (list.isEmpty()) return

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.leaderboard", statistic.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEachIndexed { i, player ->
        val position = index * 10 + i + 1
        source.sendMessage(alignText(
            local("command.leaderboard.rank", position, player.name) to 1,
            local("command.leaderboard.score", statistic.getFormattedScore(player)) to 1
        ))
    }
}