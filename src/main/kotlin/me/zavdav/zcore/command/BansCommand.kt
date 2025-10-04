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

internal val bansCommand = command(
    "bans",
    "Shows previous bans of a player or an IP address",
    "zcore.bans"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doBans(player, 1)
        }
        intArgument("page") {
            runs {
                val player: OfflinePlayer by this
                val page: Int by this
                doBans(player, page)
            }
        }
    }
    inet4AddressArgument("address") {
        runs {
            val address: Inet4Address by this
            doBans(address, 1)
        }
        intArgument("page") {
            runs {
                val address: Inet4Address by this
                val page: Int by this
                doBans(address, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBans(target: OfflinePlayer, page: Int) {
    val bans = BanList.getAllBans(target).sortedByDescending { it.timeIssued }
    val list = PagingList(bans, 5)
    if (list.isEmpty())
        throw TranslatableException("command.bans.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.bans", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))

    list.page(index).forEach {
        val issuer = it.issuer?.name ?: "Console"
        val duration = it.duration?.let { dur -> ZCore.formatDuration(dur) } ?: "permanent"
        source.sendMessage(local("command.bans.issued", issuer, ZCore.formatTimestamp(it.timeIssued)))
        source.sendMessage(local("command.bans.details", duration, it.reason, it.pardoned))
    }
}

private fun CommandContext<CommandSender>.doBans(target: Inet4Address, page: Int) {
    val bans = IpBanList.getAllBans(target).sortedByDescending { it.timeIssued }
    val list = PagingList(bans, 5)
    if (list.isEmpty())
        throw TranslatableException("command.bans.ip.none", target.hostAddress)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.bans", target.hostAddress, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))

    list.page(index).forEach {
        val issuer = it.issuer?.name ?: "Console"
        val duration = it.duration?.let { dur -> ZCore.formatDuration(dur) } ?: "permanent"
        source.sendMessage(local("command.bans.issued", issuer, ZCore.formatTimestamp(it.timeIssued)))
        source.sendMessage(local("command.bans.details", duration, it.reason, it.pardoned))
    }
}