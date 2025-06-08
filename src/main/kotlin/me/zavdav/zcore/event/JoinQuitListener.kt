package me.zavdav.zcore.event

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

internal class JoinQuitListener : Listener {

    @EventHandler
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

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player.core()
        player.data._playtime = player.data.playtime
        player.isAfk = false
    }

}