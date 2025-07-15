package me.zavdav.zcore.player

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getSafe
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncRepeatingTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.LinkedList
import java.util.UUID

/** Returns the [CorePlayer] associated with this player. */
fun Player.core(): CorePlayer = CorePlayer.get(this)

/** Represents a Bukkit [Player] with additional functionality. */
class CorePlayer(val base: Player) : Player by base {

    /** The [OfflinePlayer] associated with this player. */
    val data: OfflinePlayer = ZCore.getOfflinePlayer(uniqueId)!!

    /** Determines if this player is AFK. */
    var isAfk: Boolean = false
        internal set

    /** The player this player is replying to with /r. */
    var replyingTo: CorePlayer? = null

    /** This player's incoming teleport requests. */
    val teleportRequests = LinkedList<TeleportRequest>()

    override fun isOnline(): Boolean =
        server.onlinePlayers.any { it.uniqueId == uniqueId }

    override fun getDisplayName(): String {
        val nickname = data.nickname
        return if (nickname != null)
            "§f${ZCoreConfig.getString("general.nick-prefix").colored()}$nickname§f"
        else
            name
    }

    override fun kickPlayer(message: String) =
        base.kickPlayer(if (message.length <= 100) message else message.substring(0, 100))

    override fun teleport(location: Location): Boolean {
        location.block.chunk.load()
        vehicle?.eject()
        (base as? CraftPlayer)?.handle?.a(false, false, false)
        return base.teleport(location)
    }

    fun safelyTeleport(location: Location): Boolean {
        val safeLocation = location.getSafe() ?: return false
        teleport(safeLocation)
        return true
    }

    fun setInactive() {
        if (!isOnline || isAfk) return
        isAfk = true
        Bukkit.broadcastMessage(local("command.afk.enabled", name))
    }

    fun updateActivity() {
        if (!isOnline) return
        data.lastActivity = System.currentTimeMillis()
        base.displayName = displayName

        if (isAfk) {
            isAfk = false
            Bukkit.broadcastMessage(local("command.afk.disabled", name))
        }
    }

    private fun checkActivity() {
        if (!isOnline) return
        val inactiveTime = System.currentTimeMillis() - data.lastActivity

        val autoAfkTime = ZCoreConfig.getInt("command.afk.auto.time") * 1000L
        if (!isAfk && inactiveTime >= autoAfkTime)
            setInactive()

        if (!ZCoreConfig.getBoolean("command.afk.auto.kick.enabled")) return
        val autoKickTime = ZCoreConfig.getInt("command.afk.auto.kick.time") * 1000L
        if (isAfk && inactiveTime >= autoKickTime && !isOp && !hasPermission("zcore.afk.kick.exempt"))
            kickPlayer(local("command.afk.kick.message", formatDuration(autoKickTime)))
    }

    internal companion object {
        private val players = mutableMapOf<UUID, CorePlayer>()

        init {
            syncRepeatingTask(0, 20) {
                checkPlayerActivity()
                clearOfflinePlayers()
            }
        }

        internal fun get(player: Player): CorePlayer {
            synchronized(players) {
                var corePlayer = players[player.uniqueId]
                if (corePlayer == null) {
                    corePlayer = CorePlayer(player)
                    players[player.uniqueId] = corePlayer
                }
                return corePlayer
            }
        }

        private fun checkPlayerActivity() {
            if (!ZCoreConfig.getBoolean("command.afk.auto.enabled")) return
            synchronized(players) {
                players.forEach { (_, player) -> player.checkActivity() }
            }
        }

        private fun clearOfflinePlayers() {
            synchronized(players) {
                players.entries.removeIf { (_, player) ->
                    !player.isOnline && System.currentTimeMillis() - player.data.lastActivity > 600 * 1000
                }
            }
        }

    }

}