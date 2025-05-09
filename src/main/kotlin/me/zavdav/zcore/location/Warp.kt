package me.zavdav.zcore.location

import me.zavdav.zcore.data.Warps
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a location that users can warp to. */
class Warp(id: EntityID<UUID>) : Location(id) {

    internal companion object : UUIDEntityClass<Warp>(Warps) {
        fun new(
            name: String,
            world: String,
            x: Double,
            y: Double,
            z: Double,
            pitch: Float,
            yaw: Float
        ): Warp {
            val base = new(world, x, y, z, pitch, yaw)
            return new(base.id.value) {
                this.name = name
            }
        }
    }

    /** The name of this warp. */
    var name: String by Warps.name
        private set

}