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
import kotlin.math.floor

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

    val homes = target.homes.sortedWith { h1, h2 -> h1.name.compareTo(h2.name, true) }
    val list = PagingList(homes, 10)
    if (list.isEmpty())
        throw TranslatableException("command.homes.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.homes", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.homes.line",
            it.name, it.world, floor(it.x).toInt(), floor(it.y).toInt(), floor(it.z).toInt()))
    }
}