package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val mailCommand = command(
    "mail",
    "Shows a player's mail",
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
    offlinePlayerArgument("player") {
        requiresPermission("zcore.mail.other")
        runs {
            val player: OfflinePlayer by this
            doMail(player, 1)
        }
        intArgument("page") {
            runs {
                val player: OfflinePlayer by this
                val page: Int by this
                doMail(player, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMail(target: OfflinePlayer, page: Int) {
    val mail = target.mail.reversed()
    val list = PagingList(mail, 5)
    if (list.isEmpty())
        throw TranslatableException("command.mail.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.mail", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.mail.line", it.sender.name, it.message))
    }
}