package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import java.net.Inet4Address

internal val unbanipCommand = command(
    "unbanip",
    arrayOf("pardonip"),
    "Unbans an IP address or a player's previous IP addresses.",
    "/unbanip (<address>|<player>)",
    "zcore.unbanip"
) {
    inet4AddressArgument("address") {
        runs {
            val address: Inet4Address by this
            doUnbanip(address)
        }
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doUnbanip(target)
        }
    }
}

private fun CommandContext<CommandSender>.doUnbanip(target: Inet4Address) {
    if (IpBanList.pardonBan(target)) {
        source.sendMessage(local("command.unbanip", target.hostAddress))
    } else {
        throw TranslatableException("command.unbanip.notBanned", target.hostAddress)
    }
}

private fun CommandContext<CommandSender>.doUnbanip(target: OfflinePlayer) {
    BanList.pardonBan(target)
    target.ipAddresses.forEach { IpBanList.pardonBan(it) }
    source.sendMessage(local("command.unbanip", target.name))
}