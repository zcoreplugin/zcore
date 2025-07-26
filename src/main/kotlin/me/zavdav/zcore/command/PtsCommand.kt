package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.PagedTable
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

    val powerTools = target.powerTools.toList()
    val table = PagedTable(powerTools, 10) { _, it ->
        val materialData = MaterialData(it.material, it.data)
        arrayOf("&a${materialData.displayName} &7- &f/${it.command}" to 1)
    }

    if (table.pages() == 0)
        throw TranslatableException("command.pts.none", target.name)

    val pageNumber = page.coerceIn(1..table.pages())
    source.sendMessage(local("command.pts", target.name, pageNumber, table.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    table.print(pageNumber - 1, source)
}