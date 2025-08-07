package me.zavdav.zcore.event

import me.zavdav.zcore.command.afkCommand
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.getSafe
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs

internal class ActivityListener : Listener {

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player.core()
        val from = event.from
        val to = event.to

        if (!player.isAfk || !ZCoreConfig.getBoolean("command.afk.protect")) {
            player.updateActivity()
            return
        }

        val moved = from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ
        val cameraMoved = abs(to.pitch - from.pitch) > 1 || abs(to.yaw - from.yaw) > 1

        if (!moved) return
        if (cameraMoved) {
            player.updateActivity()
            return
        }

        var location = from
        location.pitch = to.pitch
        location.yaw = to.yaw

        location.getSafe().let {
            if (it != null)
                location = it
            else if (to.y < from.y)
                location.y = to.y
        }

        event.to = location
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onEntityDamage(event: EntityDamageEvent) {
        val player = (event.entity as? Player)?.core() ?: return
        if (player.isAfk) event.isCancelled = true
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerChat(event: PlayerChatEvent) {
        val player = event.player.core()
        player.updateActivity()
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player.core()
        val command = event.message.trimEnd()
        if (command.equals("/${afkCommand.name}", true))
            return
        player.updateActivity()
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player.core()
        player.updateActivity()
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player.core()
        player.updateActivity()
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player.core()
        player.updateActivity()
    }

    @EventHandler(priority = Event.Priority.Lowest)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player.core()
        player.updateActivity()
    }

}