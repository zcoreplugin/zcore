package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.GridPage
import me.zavdav.zcore.util.tl
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

    val pages = getPages(target)
    if (pages.isEmpty()) {
        if (self)
            throw TranslatableException("command.ignored.none")
        else
            throw TranslatableException("command.ignored.none.other")
    }

    val pageNumber = page.coerceIn(1..pages.size)
    val gridPage = pages[pageNumber - 1]
    gridPage.header = tl("command.ignored.header", pageNumber, pages.size)
    gridPage.print(source)
}

private fun getPages(player: OfflinePlayer): List<GridPage> {
    val ignored = player.ignoredPlayers.map { it.name }.sorted()
    val pages = mutableListOf<GridPage>()
    if (ignored.isEmpty()) return pages

    var currentPage = GridPage(10, 5)
    pages.add(currentPage)
    for (name in ignored) {
        if (currentPage.add(name)) continue
        currentPage = GridPage(10, 5)
        pages.add(currentPage)
    }

    return pages
}