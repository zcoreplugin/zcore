package me.zavdav.zcore.location

import me.zavdav.zcore.data.Warps
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a location that players can warp to. */
class Warp private constructor(id: EntityID<UUID>) : UUIDEntity(id), NamedLocation {

    companion object : UUIDEntityClass<Warp>(Warps)

    /** The name of this warp. */
    override var name: String by Warps.name
        internal set

    override var world: String by Warps.world
        internal set

    override var x: Double by Warps.x
        internal set

    override var y: Double by Warps.y
        internal set

    override var z: Double by Warps.z
        internal set

    override var pitch: Float by Warps.pitch
        internal set

    override var yaw: Float by Warps.yaw
        internal set

}