package me.zavdav.zcore.location

/** Represents a location with a name. */
sealed interface NamedLocation {

    /** The name of this location. */
    val name: String

    /** The world this location is in. */
    val world: String

    /** This location's x-coordinate. */
    val x: Double

    /** This location's y-coordinate. */
    val y: Double

    /** This location's z-coordinate. */
    val z: Double

    /** This location's pitch. */
    val pitch: Float

    /** This location's yaw. */
    val yaw: Float

}