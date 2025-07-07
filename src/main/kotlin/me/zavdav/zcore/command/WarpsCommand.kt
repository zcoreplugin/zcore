package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.PagedList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val warpsCommand = command(
    "warps",
    "Shows a list of warps.",
    "/warps [<page>]",
    "zcore.warps"
) {
    runs {
        doWarps(1)
    }
    intArgument("page") {
        runs {
            val page: Int by this
            doWarps(page)
        }
    }
}

private fun CommandContext<CommandSender>.doWarps(page: Int) {
    val warps = ZCore.warps.map { it.name }.sorted()
    val list = PagedList(warps, 5, 5)
    if (list.pages() == 0)
        throw TranslatableException("command.warps.none")

    val pageNumber = page.coerceIn(1..list.pages())
    source.sendMessage(local("command.warps", pageNumber, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.print(pageNumber - 1, source, ChatColor.GREEN)
}