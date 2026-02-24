package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.net.Inet4Address

internal val banhistoryCommand = command(
    "banhistory",
    "Shows previous bans of a player or an IP address",
    "zcore.banhistory"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doBanHistory(player, 1)
        }
        intArgument("page") {
            runs {
                val player: OfflinePlayer by this
                val page: Int by this
                doBanHistory(player, page)
            }
        }
    }
    inet4AddressArgument("address") {
        runs {
            val address: Inet4Address by this
            doBanHistory(address, 1)
        }
        intArgument("page") {
            runs {
                val address: Inet4Address by this
                val page: Int by this
                doBanHistory(address, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBanHistory(target: OfflinePlayer, page: Int) {
    val bans = BanList.getAllBans(target).sortedByDescending { it.timeIssued }
    val list = PagingList(bans, 5)
    if (list.isEmpty())
        throw TranslatableException("command.banhistory.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.banhistory", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))

    list.page(index).forEach {
        val issuer = it.issuer?.name ?: "Console"
        val duration = it.duration?.let { dur -> ZCore.formatDuration(dur) } ?: "permanent"
        source.sendMessage(local("command.banhistory.issued", issuer, ZCore.formatTimestamp(it.timeIssued)))
        source.sendMessage(local("command.banhistory.details", duration, it.reason, it.pardoned))
    }
}

private fun CommandContext<CommandSender>.doBanHistory(target: Inet4Address, page: Int) {
    val bans = IpBanList.getAllBans(target).sortedByDescending { it.timeIssued }
    val list = PagingList(bans, 5)
    if (list.isEmpty())
        throw TranslatableException("command.banhistory.ip.none", target.hostAddress)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.banhistory", target.hostAddress, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))

    list.page(index).forEach {
        val issuer = it.issuer?.name ?: "Console"
        val duration = it.duration?.let { dur -> ZCore.formatDuration(dur) } ?: "permanent"
        source.sendMessage(local("command.banhistory.issued", issuer, ZCore.formatTimestamp(it.timeIssued)))
        source.sendMessage(local("command.banhistory.details", duration, it.reason, it.pardoned))
    }
}