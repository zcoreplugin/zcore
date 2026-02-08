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
    "Shows the richest players",
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
    val balances = ZCore.players
        .map { it.name to it.account.balance + it.bankAccounts.sumOf { it.balance } }
        .sortedByDescending { it.second }
    val list = PagingList(balances, 10)
    if (list.isEmpty()) return

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.baltop", index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEachIndexed { i, (name, balance) ->
        val position = index * 10 + i + 1
        source.sendMessage(alignText(
            local("command.baltop.rank", position, name) to 1,
            local("command.baltop.amount", ZCore.formatCurrency(balance)) to 1
        ))
    }

    val total = balances.sumOf { it.second }
    source.sendMessage(line(ChatColor.GRAY))
    source.sendMessage(local("command.baltop.total", ZCore.formatCurrency(total)))
}