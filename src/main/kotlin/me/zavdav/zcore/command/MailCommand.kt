package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.GridPage
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val mailCommand = command(
    "mail",
    "Shows your pending mail.",
    "/mail [<page>]",
    "zcore.mail"
) {
    runs {
        val source = requirePlayer()
        doMail(source.data, 1)
    }
    intArgument("page") {
        runs {
            val source = requirePlayer()
            val page: Int by this
            doMail(source.data, page)
        }
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doMail(target, 1)
        }
        intArgument("page") {
            runs {
                val target: OfflinePlayer by this
                val page: Int by this
                doMail(target, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMail(target: OfflinePlayer, page: Int) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.mail.other")

    val pages = getPages(target)
    if (pages.isEmpty()) {
        if (self)
            throw TranslatableException("command.mail.noMail")
        else
            throw TranslatableException("command.mail.noMail.other")
    }

    val pageNumber = page.coerceIn(1..pages.size)
    val chatPage = pages[pageNumber - 1]
    chatPage.header = tl("command.mail.header", pageNumber, pages.size)
    chatPage.print(source)
}

private fun getPages(player: OfflinePlayer): List<GridPage> {
    val allMail = player.mail.reversed()
    val pages = mutableListOf<GridPage>()
    if (allMail.isEmpty()) return pages

    var currentPage = GridPage(10, 1)
    pages.add(currentPage)
    for (mail in allMail) {
        val message = tl("command.mail.message", mail.sender.name, mail.message)
        if (currentPage.add(message)) continue
        currentPage = GridPage(10, 1)
        pages.add(currentPage)
    }

    return pages
}