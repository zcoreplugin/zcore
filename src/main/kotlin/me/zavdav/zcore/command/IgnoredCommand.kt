package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PagingList
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

    val ignored = target.ignoredPlayers.sortedWith { p1, p2 -> p1.name.compareTo(p2.name, true) }
    val list = PagingList(ignored, 10)
    if (list.isEmpty())
        throw TranslatableException("command.ignored.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.ignored", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.ignored.line", it.name))
    }
}