package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val altsCommand = command(
    "alts",
    "Shows a player's alternate accounts",
    "zcore.alts"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doAlts(player, 1)
        }
        intArgument("page") {
            runs {
                val player: OfflinePlayer by this
                val page: Int by this
                doAlts(player, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doAlts(target: OfflinePlayer, page: Int) {
    val alts = target.altAccounts.sortedWith { p1, p2 -> p1.name.compareTo(p2.name, true) }
    val list = PagingList(alts, 10)
    if (list.isEmpty())
        throw TranslatableException("command.alts.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.alts", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.alts.line", it.name))
    }
}