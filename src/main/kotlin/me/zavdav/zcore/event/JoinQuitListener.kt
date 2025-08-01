package me.zavdav.zcore.event

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.IpAddresses
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerLoginEvent.Result
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.net.Inet4Address

internal class JoinQuitListener : Listener {

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val address = event.address as Inet4Address
        val ipBan = IpBanList.getActiveBan(address)

        if (ipBan != null) {
            val duration = ipBan.expiration?.let { it - System.currentTimeMillis() }
            if (duration != null) {
                event.disallow(Result.KICK_BANNED_IP,
                    local("command.banip.temporary.notify", formatDuration(duration), ipBan.reason))
            } else {
                event.disallow(Result.KICK_BANNED_IP,
                    local("command.banip.permanent.notify", ipBan.reason))
            }
            return
        }

        val player = ZCore.getOfflinePlayer(event.player.uniqueId) ?: return
        val ban = BanList.getActiveBan(player) ?: return
        val duration = ban.expiration?.let { it - System.currentTimeMillis() }

        if (duration != null) {
            event.disallow(Result.KICK_BANNED,
                local("command.ban.temporary.notify", formatDuration(duration), ban.reason))
        } else {
            event.disallow(Result.KICK_BANNED,
                local("command.ban.permanent.notify", ban.reason))
        }
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        var player = ZCore.getOfflinePlayer(event.player.uniqueId)
        val now = System.currentTimeMillis()

        if (player == null) {
            player = OfflinePlayer.new(event.player.uniqueId) {
                this.name = event.player.name
                this.firstJoin = now
                this.lastJoin = now
                this.lastActivity = now
            }

            PersonalAccount.new {
                this.owner = player
            }
        }

        player.lastJoin = now
        player.lastActivity = now

        val ipAddress = event.player.address.address as Inet4Address
        val entryExists = !IpAddresses.selectAll().where {
            (IpAddresses.player eq player.id) and (IpAddresses.ipAddress eq ipAddress)
        }.empty()

        if (!entryExists) {
            IpAddresses.insert {
                it[IpAddresses.player] = player.id
                it[IpAddresses.ipAddress] = ipAddress
            }
        }

        for (pl in Bukkit.getOnlinePlayers()) {
            if (pl.core().data.isVanished) {
                Bukkit.getOnlinePlayers()
                    .filter { !it.isOp && !it.hasPermission("zcore.vanish.bypass") }
                    .forEach { it.hidePlayer(pl) }
            } else {
                Bukkit.getOnlinePlayers().forEach { it.showPlayer(pl) }
            }
        }
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player.core()
        player.data._playtime = player.data.playtime
        player.isAfk = false
    }

}