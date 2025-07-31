package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.net.Inet4Address

internal val banipCommand = command(
    "banip",
    "Bans an IP address or a player's previous IP addresses.",
    "/banip (<address>|<player>) [<duration>] [<reason>]",
    "zcore.banip"
) {
    inet4AddressArgument("address") {
        runs {
            val address: Inet4Address by this
            doBanip(address, null, ZCoreConfig.getString("command.banip.default-reason"))
        }
        durationArgument("duration") {
            runs {
                val address: Inet4Address by this
                val duration: Long by this
                doBanip(address, duration, ZCoreConfig.getString("command.banip.default-reason"))
            }
            textArgument("reason") {
                runs {
                    val address: Inet4Address by this
                    val duration: Long by this
                    val reason: String by this
                    doBanip(address, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val address: Inet4Address by this
                val reason: String by this
                doBanip(address, null, reason)
            }
        }
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doBanip(target, null, ZCoreConfig.getString("command.banip.default-reason"))
        }
        durationArgument("duration") {
            runs {
                val target: OfflinePlayer by this
                val duration: Long by this
                doBanip(target, duration, ZCoreConfig.getString("command.banip.default-reason"))
            }
            textArgument("reason") {
                runs {
                    val target: OfflinePlayer by this
                    val duration: Long by this
                    val reason: String by this
                    doBanip(target, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val target: OfflinePlayer by this
                val reason: String by this
                doBanip(target, null, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBanip(target: Inet4Address, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    IpBanList.addBan(target, issuer, duration, reason)
    Bukkit.getOnlinePlayers()
        .filter { it.address.address == target }
        .map { it.core() }
        .forEach {
            if (duration != null) {
                it.kickPlayer(local("command.banip.temporary.notify", formatDuration(duration), reason))
            } else {
                it.kickPlayer(local("command.banip.permanent.notify", reason))
            }
        }

    if (duration != null) {
        source.sendMessage(local("command.banip.temporary",
            target.hostAddress, formatDuration(duration), reason))
    } else {
        source.sendMessage(local("command.banip.permanent", target.hostAddress, reason))
    }
}

private fun CommandContext<CommandSender>.doBanip(target: OfflinePlayer, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    BanList.addBan(target, issuer, duration, reason)
    target.ipAddresses.forEach { IpBanList.addBan(it, issuer, duration, reason) }
    Bukkit.getOnlinePlayers()
        .filter { target.ipAddresses.any { addr -> it.address.address == addr } }
        .map { it.core() }
        .forEach {
            if (duration != null) {
                it.kickPlayer(local("command.banip.temporary.notify", formatDuration(duration), reason))
            } else {
                it.kickPlayer(local("command.banip.permanent.notify", reason))
            }
        }

    if (duration != null) {
        source.sendMessage(local("command.banip.temporary", target.name, formatDuration(duration), reason))
    } else {
        source.sendMessage(local("command.banip.permanent", target.name, reason))
    }
}