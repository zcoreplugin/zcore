package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.PagedTable
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val baltopCommand = command(
    "baltop",
    "Shows the players with the highest balances.",
    "/baltop [<page>]",
    "zcore.baltop"
) {
    runs {
        doBaltop(1)
    }
    intArgument("page") {
        runs {
            val page: Int by this
            doBaltop(page)
        }
    }
}

private fun CommandContext<CommandSender>.doBaltop(page: Int) {
    val source = this.source
    val players = ZCore.players.sortedByDescending { it.account.balance }
    val top = PagedTable(players, 10) { i, player -> arrayOf(
        local("command.baltop.rank", i + 1, player.name) to 1,
        local("command.baltop.amount", ZCore.formatCurrency(player.account.balance)) to 1
    ) }

    if (top.pages() == 0) return
    val pageNumber = page.coerceIn(1..top.pages())

    source.sendMessage(local("command.baltop", pageNumber, top.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    top.print(pageNumber - 1, source)

    val total = players.sumOf { it.account.balance }
    source.sendMessage(line(ChatColor.GRAY))
    source.sendMessage(local("command.baltop.total", ZCore.formatCurrency(total)))
}