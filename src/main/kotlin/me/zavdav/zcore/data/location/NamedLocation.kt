package me.zavdav.zcore.data.location

import org.bukkit.Location
import org.bukkit.World

/**
 * Represents a [Location] with a name.
 * In a collection of named locations, there are not allowed to be
 * two elements whose names are equal when casing is ignored.
 */
class NamedLocation(
    val name: String,
    world: World,
    x: Double,
    y: Double,
    z: Double,
    pitch: Float = 0f,
    yaw: Float = 0f
): Location(world, x, y, z, yaw, pitch) {

    constructor(name: String, location: Location) : this(
        name, location.world, location.x, location.y, location.z, location.pitch, location.yaw
    )

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj !is NamedLocation) return false
        return name == obj.name && super.equals(obj.clone())
    }

    override fun hashCode(): Int {
        var hash = super.hashCode()
        hash = 19 * hash + name.hashCode()
        return hash
    }

}