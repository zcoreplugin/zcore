package me.zavdav.zcore.event

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.player.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (ZCore.getOfflinePlayer(event.player.uniqueId) == null) {
            val now = System.currentTimeMillis()

            val player = OfflinePlayer.new(event.player.uniqueId) {
                this.name = event.player.name
                this.firstJoin = now
                this.lastJoin = now
                this.lastOnline = now
            }

            PersonalAccount.new {
                this.owner = player
            }
        }
    }

}