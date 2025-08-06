package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val ptsCommand = command(
    "pts",
    "Shows a player's power tools.",
    "/pts [<player>] [<page>]",
    "zcore.pts"
) {
    runs {
        val source = requirePlayer()
        doPts(source.data, 1)
    }
    intArgument("page") {
        runs {
            val source = requirePlayer()
            val page: Int by this
            doPts(source.data, page)
        }
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doPts(target, 1)
        }
        intArgument("page") {
            runs {
                val target: OfflinePlayer by this
                val page: Int by this
                doPts(target, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doPts(target: OfflinePlayer, page: Int) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.pts.other")

    val powerTools = target.powerTools.sortedWith { p1, p2 ->
        val m1 = MaterialData(p1.material, p1.data)
        val m2 = MaterialData(p2.material, p2.data)
        m1.displayName.compareTo(m2.displayName, true)
    }
    val list = PagingList(powerTools, 10)
    if (list.isEmpty())
        throw TranslatableException("command.pts.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.pts", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        val materialData = MaterialData(it.material, it.data)
        source.sendMessage(local("command.pts.line", materialData.displayName, it.command))
    }
}