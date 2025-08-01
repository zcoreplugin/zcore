package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.PagedList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val listCommand = command(
    "list",
    arrayOf("online"),
    "Shows all connected players.",
    "/list [<page>]",
    "zcore.list"
) {
    runs {
        doList(1)
    }
    intArgument("page") {
        runs {
            val page: Int by this
            doList(page)
        }
    }
}

private fun CommandContext<CommandSender>.doList(page: Int) {
    val players = Bukkit.getOnlinePlayers()
        .sortedWith { p1, p2 -> p1.name.compareTo(p2.name, true)}
        .map { it.displayName }

    val list = PagedList(players, 10, 2)
    val pages = list.pages().coerceAtLeast(1)
    val pageNumber = page.coerceIn(1..pages)

    source.sendMessage(local("command.list", players.size, pageNumber, pages))
    source.sendMessage(line(ChatColor.GRAY))
    if (list.pages() == 0) return
    list.print(pageNumber - 1, source)
}