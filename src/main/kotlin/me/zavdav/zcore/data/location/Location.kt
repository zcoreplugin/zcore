package me.zavdav.zcore.data.location

import me.zavdav.zcore.data.Locations
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a location in a world. */
sealed class Location(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Location>(Locations)

    /** The world this location is in. */
    var world: String by Locations.world
        internal set

    /** This location's x-coordinate. */
    var x: Double by Locations.x
        internal set

    /** This location's y-coordinate. */
    var y: Double by Locations.y
        internal set

    /** This location's z-coordinate. */
    var z: Double by Locations.z
        internal set

    /** This location's pitch. */
    var pitch: Float by Locations.pitch
        internal set

    /** This location's yaw. */
    var yaw: Float by Locations.yaw
        internal set

}