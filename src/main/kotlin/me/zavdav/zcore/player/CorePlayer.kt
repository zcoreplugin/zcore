package me.zavdav.zcore.player

import me.zavdav.zcore.ZCore
import org.bukkit.Bukkit
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

    override fun isOnline(): Boolean =
        server.onlinePlayers.any { it.uniqueId == uniqueId }

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