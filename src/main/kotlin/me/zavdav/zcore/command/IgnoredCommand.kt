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

internal val ignoredCommand = command(
    "ignored",
    "Shows a player's ignored players.",
    "/ignored [<player>] [<page>]",
    "zcore.ignored"
) {
    runs {
        val source = requirePlayer()
        doIgnored(source.data, 1)
    }
    intArgument("page") {
        runs {
            val source = requirePlayer()
            val page: Int by this
            doIgnored(source.data, page)
        }
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doIgnored(target, 1)
        }
        intArgument("page") {
            runs {
                val target: OfflinePlayer by this
                val page: Int by this
                doIgnored(target, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doIgnored(target: OfflinePlayer, page: Int) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.ignored.other")

    val ignored = target.ignoredPlayers.map { it.name }.sorted()
    val list = PagedList(ignored, 5, 4)
    if (list.pages() == 0)
        throw TranslatableException("command.ignored.none", target.name)

    val pageNumber = page.coerceIn(1..list.pages())
    source.sendMessage(local("command.ignored", target.name, pageNumber, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.print(pageNumber - 1, source, ChatColor.GREEN)
}