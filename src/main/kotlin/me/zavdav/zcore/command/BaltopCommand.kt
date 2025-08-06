package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.alignText
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
    val players = ZCore.players.sortedByDescending { it.account.balance }
    val list = PagingList(players, 10)
    if (list.isEmpty()) return

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.baltop", index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEachIndexed { i, it ->
        val position = index * 10 + i + 1
        source.sendMessage(alignText(
            local("command.baltop.rank", position, it.name) to 1,
            local("command.baltop.amount", ZCore.formatCurrency(it.account.balance)) to 1
        ))
    }

    val total = players.sumOf { it.account.balance }
    source.sendMessage(line(ChatColor.GRAY))
    source.sendMessage(local("command.baltop.total", ZCore.formatCurrency(total)))
}