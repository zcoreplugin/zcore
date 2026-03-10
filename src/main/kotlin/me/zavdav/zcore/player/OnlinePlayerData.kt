package me.zavdav.zcore.player

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.inventory.InventoryView
import me.zavdav.zcore.group.GroupResolver
import me.zavdav.zcore.util.computeNickname
import me.zavdav.zcore.util.formatted
import me.zavdav.zcore.util.getSafe
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncRepeatingTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.CreatureType
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.LinkedList
import java.util.UUID
import kotlin.math.min

private val Player.onlineData: OnlinePlayerData
    get() = OnlinePlayerData.get(this)

val Player.data: OfflinePlayer
    get() = onlineData.data

/** Determines if this player is AFK. */
var Player.isAfk: Boolean
    get() = onlineData.isAfk
    internal set(value) {
        onlineData.isAfk = value
    }

/** The player this player is replying to with /r. */
var Player.replyingTo: Player?
    get() = onlineData.replyingTo
    internal set(value) {
        onlineData.replyingTo = value
    }

/** This player's incoming teleport requests. */
val Player.teleportRequests
    get() = onlineData.teleportRequests

internal val Player.bankInvites: MutableMap<BankAccount, Player>
    get() = onlineData.bankInvites

internal var Player.inventoryView: InventoryView?
    get() = onlineData.inventoryView
    set(value) {
        onlineData.inventoryView = value
    }

internal var Player.spawnerType: CreatureType?
    get() = onlineData.spawnerType
    set(value) {
        onlineData.spawnerType = value
    }

internal var Player.lastPowerToolUse: Long
    get() = onlineData.lastPowerToolUse
    set(value) {
        onlineData.lastPowerToolUse = value
    }

val Player.zcoreDisplayName: String
    get() = "§f${formatted(ZCoreConfig.getString("text.display-name-format"),
        "prefix" to GroupResolver.getPrefix(data),
        "nickname" to computeNickname(data),
        "suffix" to GroupResolver.getSuffix(data)
    ).trim()}§f"

fun Player.kick(message: String) =
    kickPlayer(message.substring(0, min(message.length, 100)))

fun Player.teleportTo(location: Location): Boolean {
    location.block.chunk.load()
    vehicle?.eject()
    (this as? CraftPlayer)?.handle?.a(false, false, false)
    return teleport(location)
}

fun Player.teleportTo(entity: Entity): Boolean {
    return teleportTo(entity.location)
}

fun Player.teleportSafelyTo(location: Location): Boolean {
    val safeLocation = location.getSafe() ?: return false
    return teleportTo(safeLocation)
}

fun Player.setInactive() {
    if (!isOnline || isAfk) return
    isAfk = true
    Bukkit.broadcastMessage(local("command.afk.enabled", name))
}

fun Player.updateActivity() {
    if (!isOnline) return
    data.lastActivity = System.currentTimeMillis()
    displayName = zcoreDisplayName

    if (isAfk) {
        isAfk = false
        Bukkit.broadcastMessage(local("command.afk.disabled", name))
    }
}

private class OnlinePlayerData(private val uuid: UUID) {

    val data: OfflinePlayer = ZCore.getOfflinePlayer(uuid)!!
    var isAfk: Boolean = false
    var replyingTo: Player? = null
    val teleportRequests = LinkedList<TeleportRequest>()
    val bankInvites = mutableMapOf<BankAccount, Player>()
    var inventoryView: InventoryView? = null
    var spawnerType: CreatureType? = null
    var lastPowerToolUse: Long = 0

    val isOnline: Boolean
        get() = Bukkit.getOnlinePlayers().any { it.uniqueId == uuid }

    private fun checkActivity() {
        val player = ZCore.getPlayer(uuid) ?: return
        val inactiveTime = System.currentTimeMillis() - data.lastActivity

        val autoAfkTime = ZCoreConfig.getInt("command.afk.auto.time") * 1000L
        if (!isAfk && inactiveTime >= autoAfkTime)
            player.setInactive()

        if (!ZCoreConfig.getBoolean("command.afk.auto.kick.enabled")) return
        val autoKickTime = ZCoreConfig.getInt("command.afk.auto.kick.time") * 1000L
        if (isAfk && inactiveTime >= autoKickTime && !player.hasPermission("zcore.afk.kick.exempt"))
            player.kick(local("command.afk.kick.message", ZCore.formatDuration(autoKickTime)))
    }

    companion object {
        private val map = mutableMapOf<UUID, OnlinePlayerData>()

        init {
            syncRepeatingTask(0, 20) { checkPlayerActivity() }
        }

        fun get(player: Player): OnlinePlayerData {
            synchronized(map) {
                var onlineData = map[player.uniqueId]
                if (onlineData == null) {
                    onlineData = OnlinePlayerData(player.uniqueId)
                    map[player.uniqueId] = onlineData
                }
                return onlineData
            }
        }

        private fun checkPlayerActivity() {
            synchronized(map) {
                if (ZCoreConfig.getBoolean("command.afk.auto.enabled")) {
                    map.forEach { (_, onlineData) -> onlineData.checkActivity() }
                }
                map.entries.removeIf { (_, onlineData) ->
                    !onlineData.isOnline && System.currentTimeMillis() - onlineData.data.lastActivity > 600 * 1000
                }
            }
        }
    }

}