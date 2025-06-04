package me.zavdav.zcore.player

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.getSafe
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.UUID

/** Returns the [CorePlayer] associated with this player. */
fun Player.core(): CorePlayer = CorePlayer.get(this)

/** Represents a Bukkit [Player] with additional functionality. */
class CorePlayer(val base: Player) : Player by base {

    /** The [OfflinePlayer] associated with this player. */
    val data: OfflinePlayer = ZCore.getOfflinePlayer(uniqueId)!!

    /** Determines if this player is AFK. */
    var isAfk: Boolean = false

    /** The player this player is replying to with /r. */
    var replyingTo: CorePlayer? = null

    override fun isOnline(): Boolean =
        server.onlinePlayers.any { it.uniqueId == uniqueId }

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

    /** Sends a private [message] to a [target] player. */
    fun privateMessage(target: CorePlayer, message: String) {
        replyingTo = target
        target.replyingTo = this
        sendMessage(tl("command.msg.toPlayer", target.displayName, message))
        target.sendMessage(tl("command.msg.fromPlayer", displayName, message))
    }

    internal companion object {
        private val players = mutableMapOf<UUID, CorePlayer>()

        init {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(
                ZCore.INSTANCE,
                {
                    synchronized(players) {
                        players.entries.removeIf { (_, player) ->
                            !player.isOnline && System.currentTimeMillis() - player.data.lastOnline > 600 * 1000
                        }
                    }
                },
                0, 20
            )
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

    }

}