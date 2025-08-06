package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import kotlin.math.floor

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
    val warps = ZCore.warps.sortedWith { w1, w2 -> w1.name.compareTo(w2.name, true) }
    val list = PagingList(warps, 10)
    if (list.isEmpty())
        throw TranslatableException("command.warps.none")

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.warps", index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.warps.line",
            it.name, it.world, floor(it.x).toInt(), floor(it.y).toInt(), floor(it.z).toInt()))
    }
}