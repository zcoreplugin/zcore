package me.zavdav.zcore.event

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.tl
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerLoginEvent.Result
import org.bukkit.event.player.PlayerQuitEvent

internal class JoinQuitListener : Listener {

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = ZCore.getOfflinePlayer(event.player.uniqueId) ?: return
        val ban = BanList.getActiveBan(player) ?: return
        val duration = ban.expiration?.let { it - System.currentTimeMillis() }

        if (duration != null)
            event.disallow(Result.KICK_BANNED, tl("command.ban.temporary.message", formatDuration(duration), ban.reason))
        else
            event.disallow(Result.KICK_BANNED, tl("command.ban.permanent.message", ban.reason))
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
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player.core()
        player.data._playtime = player.data.playtime
        player.isAfk = false
    }

}