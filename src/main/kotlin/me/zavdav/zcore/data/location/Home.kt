package me.zavdav.zcore.data.location

import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a home that belongs to a user. */
class Home(id: EntityID<UUID>) : Location(id) {

    internal companion object : UUIDEntityClass<Home>(Homes) {
        fun new(
            user: OfflineUser,
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
                this.user = user
                this.name = name
            }
        }
    }

    /** The user this home belongs to. */
    var user by OfflineUser referencedOn Homes.user
        private set

    /** The name of this home. */
    var name: String by Homes.name
        private set

}