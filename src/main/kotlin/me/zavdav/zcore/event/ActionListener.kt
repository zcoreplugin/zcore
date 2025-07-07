package me.zavdav.zcore.event

import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.formatted
import me.zavdav.zcore.util.local
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent

internal class ActionListener : Listener {

    @EventHandler(priority = Event.Priority.Low, ignoreCancelled = true)
    fun onPlayerChat(event: PlayerChatEvent) {
        val player = event.player.core()
        val mute = MuteList.getActiveMute(player.data)

        if (mute != null) {
            val duration = mute.expiration?.let { it - System.currentTimeMillis() }
            if (duration != null)
                player.sendMessage(local("command.mute.temporary.message", formatDuration(duration), mute.reason))
            else
                player.sendMessage(local("command.mute.permanent.message", mute.reason))

            event.isCancelled = true
            return
        }

        if (player.isOp || player.hasPermission("zcore.chat.color"))
            event.message = event.message.colored()

        event.format = formatted(ZCoreConfig.getString("general.chat-format"),
            "player" to "%1\$s", "message" to "%2\$s"
        )

        event.recipients.removeIf {
            it.core().data.ignores(player.data) && !player.isOp && !player.hasPermission("zcore.ignore.bypass")
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        val player = (event.entity as? Player)?.core() ?: return
        if (player.data.isInvincible) {
            player.fireTicks = 0
            player.remainingAir = player.maximumAir
            event.isCancelled = true
        }
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onEntityTarget(event: EntityTargetEvent) {
        val player = (event.target as? Player)?.core()?.data ?: return
        if (player.isVanished) event.isCancelled = true
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player.core().data
        if (player.isVanished) event.isCancelled = true
    }

    @EventHandler(priority = Event.Priority.Lowest, ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val player = event.player.core().data
        if (player.isVanished) event.isCancelled = true
    }

}