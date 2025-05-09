package me.zavdav.zcore.location

import me.zavdav.zcore.data.Locations
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a location in a world. */
sealed class Location(id: EntityID<UUID>): UUIDEntity(id) {

    internal companion object : UUIDEntityClass<Location>(Locations) {
        fun new(
            world: String,
            x: Double,
            y: Double,
            z: Double,
            pitch: Float,
            yaw: Float
        ): Location =
            new {
                this.world = world
                this.x = x
                this.y = y
                this.z = z
                this.pitch = pitch
                this.yaw = yaw
            }
    }

    /** The world this location is in. */
    var world: String by Locations.world
        private set

    /** This location's x-coordinate. */
    var x: Double by Locations.x
        private set

    /** This location's y-coordinate. */
    var y: Double by Locations.y
        private set

    /** This location's z-coordinate. */
    var z: Double by Locations.z
        private set

    /** This location's pitch. */
    var pitch: Float by Locations.pitch
        private set

    /** This location's yaw. */
    var yaw: Float by Locations.yaw
        private set

}