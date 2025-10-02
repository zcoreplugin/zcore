package me.zavdav.zcore.listener

import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.syncDelayedTask
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerMoveEvent

internal class StatisticsListener : Listener {

    @EventHandler(priority = Event.Priority.Monitor, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player.core().data
        player.blocksPlaced++
    }

    @EventHandler(priority = Event.Priority.Monitor, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player.core().data
        player.blocksBroken++
    }

    @EventHandler(priority = Event.Priority.Monitor, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val distance = event.from.distance(event.to)
        if (distance < 0.05) return
        val player = event.player.core().data
        player.blocksTraveled += distance.toBigDecimal()
    }

    @EventHandler(priority = Event.Priority.Monitor, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        val damagee = event.entity as? LivingEntity ?: return
        val prevHealth = damagee.health

        if (damagee is Player) {
            val player = damagee.core().data
            syncDelayedTask(1) {
                val damage = prevHealth - damagee.health.coerceAtLeast(0)
                if (damage > 0) player.damageTaken += damage
            }
        }

        if (event is EntityDamageByEntityEvent) {
            val damager = when (event.cause) {
                DamageCause.ENTITY_ATTACK -> event.damager as? Player
                DamageCause.PROJECTILE -> (event.damager as? Projectile)?.shooter as? Player
                else -> null
            }

            val player = damager?.core()?.data ?: return
            syncDelayedTask(1) {
                val damage = prevHealth - damagee.health.coerceAtLeast(0)
                if (damage > 0) player.damageDealt += damage
            }
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        var isPlayer = false

        if (entity is Player) {
            isPlayer = true
            val player = entity.core().data
            player.deaths++
        }

        val cause = entity.lastDamageCause
        if (cause is EntityDamageByEntityEvent) {
            val damager = when (cause.cause) {
                DamageCause.ENTITY_ATTACK -> cause.damager as? Player
                DamageCause.PROJECTILE -> (cause.damager as? Projectile)?.shooter as? Player
                else -> null
            }

            val player = damager?.core()?.data ?: return
            if (isPlayer) {
                player.playersKilled++
            } else {
                player.mobsKilled++
            }
        }
    }

}