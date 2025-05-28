package me.zavdav.zcore.location

import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a home that belongs to a player. */
class Home(id: EntityID<UUID>) : Location(id) {

    internal companion object : UUIDEntityClass<Home>(Homes) {
        fun new(
            player: OfflinePlayer,
            name: String,
            world: String,
            x: Double,
            y: Double,
            z: Double,
            pitch: Float,
            yaw: Float
        ): Home {
            val base = new(world, x, y, z, pitch, yaw)
            return new(base.id.value) {
                this.player = player
                this.name = name
            }
        }
    }

    /** The player this home belongs to. */
    var player by OfflinePlayer referencedOn Homes.player
        private set

    /** The name of this home. */
    var name: String by Homes.name
        private set

}