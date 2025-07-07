package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PagedList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val homesCommand = command(
    "homes",
    arrayOf("hl"),
    "Shows a list of your homes.",
    "/homes [<page>]",
    "zcore.homes"
) {
    runs {
        val source = requirePlayer()
        doHomes(source.data, 1)
    }
    intArgument("page") {
        runs {
            val source = requirePlayer()
            val page: Int by this
            doHomes(source.data, page)
        }
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doHomes(target, 1)
        }
        intArgument("page") {
            runs {
                val target: OfflinePlayer by this
                val page: Int by this
                doHomes(target, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doHomes(target: OfflinePlayer, page: Int) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.homes.other")

    val homes = target.homes.map { it.name }.sorted()
    val list = PagedList(homes, 5, 5)
    if (list.pages() == 0)
        throw TranslatableException("command.homes.none", target.name)

    val pageNumber = page.coerceIn(1..list.pages())
    source.sendMessage(local("command.homes", target.name, pageNumber, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.print(pageNumber - 1, source, ChatColor.GREEN)
}