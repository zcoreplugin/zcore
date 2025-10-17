package me.zavdav.zcore.listener

import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.checkMuted
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.displayName
import me.zavdav.zcore.util.formatted
import me.zavdav.zcore.util.local
import net.minecraft.server.Packet102WindowClick
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.packet.PacketReceivedEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent

internal class ActionListener : Listener {

    @EventHandler(priority = Event.Priority.Low, ignoreCancelled = true)
    fun onPlayerChat(event: PlayerChatEvent) {
        val player = event.player.core()
        if (player.hasPermission("zcore.chat.color"))
            event.message = event.message.colored()

        if (player.checkMuted()) {
            event.isCancelled = true
            return
        }

        event.format = formatted(ZCoreConfig.getString("text.chat-format"),
            "player" to "%1\$s", "message" to "%2\$s")
        event.recipients.removeIf { it.core().data.checkIgnoring(player) }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player.core()
        val creature = player.spawnerType ?: return
        val blockState = event.clickedBlock?.state ?: return

        player.spawnerType = null
        if (blockState !is CreatureSpawner) {
            player.sendMessage(local("command.spawner.cancelled"))
            return
        }

        blockState.creatureType = creature
        player.sendMessage(local("command.spawner", creature.displayName))
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerAnimation(event: PlayerAnimationEvent) {
        val player = event.player.core()
        val item = player.itemInHand
        if (item.type == Material.AIR) return

        val powerTool = player.data.getPowerTool(item.type, item.durability) ?: return
        if (powerTool.command.contains("<player>", true))
            return

        val now = System.currentTimeMillis()
        if (now - player.lastPowerToolUse <= 200) return
        player.lastPowerToolUse = now
        player.performCommand(powerTool.command)
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onEntityDamage(event: EntityDamageEvent) {
        val target = (event.entity as? Player)?.core() ?: return
        if (target.data.isInvincible && target.hasPermission("zcore.god")) {
            target.fireTicks = 0
            target.remainingAir = target.maximumAir
            event.isCancelled = true
        }

        if (event !is EntityDamageByEntityEvent) return
        val damager = (event.damager as? Player)?.core() ?: return
        val item = damager.itemInHand
        if (item.type == Material.AIR) return

        val powerTool = damager.data.getPowerTool(item.type, item.durability) ?: return
        if (!powerTool.command.contains("<player>", true))
            return

        val now = System.currentTimeMillis()
        if (now - damager.lastPowerToolUse <= 200) return
        damager.lastPowerToolUse = now
        damager.performCommand(powerTool.command.replaceFirst("<player>", target.name, true))
        event.isCancelled = true
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

    @EventHandler(priority = Event.Priority.Low)
    fun onPacketReceived(event: PacketReceivedEvent) {
        val player = event.player.core()
        if (player.isDead) return
        val packet = event.packet as? Packet102WindowClick ?: return

        val view = player.inventoryView ?: return
        event.isCancelled = true
        view.handleClick(packet)
    }

    @EventHandler(priority = Event.Priority.Low, ignoreCancelled = true)
    fun onSignChange(event: SignChangeEvent) {
        val player = event.player.core()
        if (player.hasPermission("zcore.sign.color")) {
            for (i in event.lines.indices) {
                event.setLine(i, event.getLine(i).colored())
            }
        }
    }

}