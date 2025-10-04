package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.command.event.IpBanEvent
import me.zavdav.zcore.command.event.PlayerBanEvent
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.net.Inet4Address

internal val banipCommand = command(
    "banip",
    "Bans an IP address or a player's previous IP addresses",
    "zcore.banip"
) {
    inet4AddressArgument("address") {
        runs {
            val address: Inet4Address by this
            doBanIp(address, null, ZCoreConfig.getString("command.banip.default-reason"))
        }
        durationArgument("duration") {
            runs {
                val address: Inet4Address by this
                val duration: Long by this
                doBanIp(address, duration, ZCoreConfig.getString("command.banip.default-reason"))
            }
            textArgument("reason") {
                runs {
                    val address: Inet4Address by this
                    val duration: Long by this
                    val reason: String by this
                    doBanIp(address, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val address: Inet4Address by this
                val reason: String by this
                doBanIp(address, null, reason)
            }
        }
    }
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doBanIp(player, null, ZCoreConfig.getString("command.banip.default-reason"))
        }
        durationArgument("duration") {
            runs {
                val player: OfflinePlayer by this
                val duration: Long by this
                doBanIp(player, duration, ZCoreConfig.getString("command.banip.default-reason"))
            }
            textArgument("reason") {
                runs {
                    val player: OfflinePlayer by this
                    val duration: Long by this
                    val reason: String by this
                    doBanIp(player, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val player: OfflinePlayer by this
                val reason: String by this
                doBanIp(player, null, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBanIp(target: Inet4Address, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    if (!IpBanEvent(source, target, duration, reason).call()) return
    IpBanList.addBan(target, issuer, duration, reason)
    Bukkit.getOnlinePlayers()
        .filter { it.address.address == target }
        .map { it.core() }
        .forEach {
            if (duration != null) {
                it.kickPlayer(local("command.banip.temporary.notify", ZCore.formatDuration(duration), reason))
            } else {
                it.kickPlayer(local("command.banip.permanent.notify", reason))
            }
        }

    if (duration != null) {
        source.sendMessage(local("command.banip.temporary",
            target.hostAddress, ZCore.formatDuration(duration), reason))
    } else {
        source.sendMessage(local("command.banip.permanent", target.hostAddress, reason))
    }
}

private fun CommandContext<CommandSender>.doBanIp(target: OfflinePlayer, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    if (Bukkit.getOfflinePlayer(target.name).isOp)
        throw TranslatableException("command.banip.exempt", target.name)
    if (!PlayerBanEvent(source, target, duration, reason).call()) return

    BanList.addBan(target, issuer, duration, reason)
    target.ipAddresses.forEach { IpBanList.addBan(it, issuer, duration, reason) }
    Bukkit.getOnlinePlayers()
        .filter { target.ipAddresses.any { addr -> it.address.address == addr } }
        .map { it.core() }
        .forEach {
            if (duration != null) {
                it.kickPlayer(local("command.banip.temporary.notify", ZCore.formatDuration(duration), reason))
            } else {
                it.kickPlayer(local("command.banip.permanent.notify", reason))
            }
        }

    if (duration != null) {
        source.sendMessage(local("command.banip.temporary", target.name, ZCore.formatDuration(duration), reason))
    } else {
        source.sendMessage(local("command.banip.permanent", target.name, reason))
    }
}