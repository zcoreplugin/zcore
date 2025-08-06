package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.formatTimestamp
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val mutesCommand = command(
    "mutes",
    "Shows previous mutes of a player.",
    "/mutes <player> [<page>]",
    "zcore.mutes"
) {
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doMutes(target, 1)
        }
        intArgument("page") {
            runs {
                val target: OfflinePlayer by this
                val page: Int by this
                doMutes(target, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMutes(target: OfflinePlayer, page: Int) {
    val mutes = MuteList.getAllMutes(target).sortedByDescending { it.timeIssued }
    val list = PagingList(mutes, 5)
    if (list.isEmpty())
        throw TranslatableException("command.mutes.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.mutes", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))

    list.page(index).forEach {
        val issuer = it.issuer?.name ?: "Console"
        val duration = it.duration?.let { dur -> formatDuration(dur) } ?: "permanent"
        source.sendMessage(local("command.mutes.issued", issuer, formatTimestamp(it.timeIssued)))
        source.sendMessage(local("command.mutes.details", duration, it.reason, it.pardoned))
    }
}