package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val listCommand = command(
    "list",
    arrayOf("online"),
    "Shows all online players",
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
    val list = PagingList(players, 10)
    val pages = list.pages().coerceAtLeast(1)
    val index = page.coerceIn(1..pages) - 1

    source.sendMessage(local("command.list", players.size, index + 1, pages))
    source.sendMessage(line(ChatColor.GRAY))
    if (list.isEmpty()) return
    list.page(index).forEach {
        source.sendMessage(local("command.list.line", it.displayName))
    }
}