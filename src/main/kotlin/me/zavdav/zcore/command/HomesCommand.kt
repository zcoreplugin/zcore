package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.GridPage
import me.zavdav.zcore.util.tl
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

    val pages = getPages(target)
    if (pages.isEmpty()) {
        if (self)
            throw TranslatableException("command.homes.noHomes")
        else
            throw TranslatableException("command.homes.noHomes.other")
    }

    val pageNumber = page.coerceIn(1..pages.size)
    val chatPage = pages[pageNumber - 1]
    chatPage.header = tl("command.homes.header", pageNumber, pages.size)
    chatPage.print(source)
}

private fun getPages(player: OfflinePlayer): List<GridPage> {
    val homes = player.homes.map { it.name }.sorted()
    val pages = mutableListOf<GridPage>()
    if (homes.isEmpty()) return pages

    var currentPage = GridPage(10, 5)
    pages.add(currentPage)
    for (home in homes) {
        if (currentPage.add(home)) continue
        currentPage = GridPage(10, 5)
        pages.add(currentPage)
    }

    return pages
}