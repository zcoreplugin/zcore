package me.zavdav.zcore.event

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.data.IpAddresses
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.formatted
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.updateVanishStates
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
            PersonalAccount.new { this.owner = player }
            Bukkit.broadcastMessage(formatted(
                ZCoreConfig.getString("text.new-player-announcement"),
                "player" to event.player.name
            ))
        }

        player.name = event.player.name
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

        updateVanishStates()

        ZCoreConfig.getStringList("text.message-of-the-day").forEach {
            event.player.sendMessage(formatted(
                it,
                "name" to event.player.name,
                "displayname" to event.player.displayName,
                "playercount" to Bukkit.getOnlinePlayers().size,
                "maxplayers" to Bukkit.getMaxPlayers()
            ))
        }

        if (!player.mail.empty()) {
            event.player.sendMessage(local("command.mail.pending"))
        }
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player.core()
        player.data._playtime = player.data.playtime
        player.isAfk = false
    }

}