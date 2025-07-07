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

    val mail = target.mail.reversed().map {
        local("command.mail.message", it.sender.name, it.message)
    }
    val list = PagedList(mail, 5, 1)
    if (list.pages() == 0)
        throw TranslatableException("command.mail.none", target.name)

    val pageNumber = page.coerceIn(1..list.pages())
    source.sendMessage(local("command.mail", target.name, pageNumber, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.print(pageNumber - 1, source)
}