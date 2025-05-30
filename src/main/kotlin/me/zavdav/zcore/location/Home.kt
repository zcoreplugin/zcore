package me.zavdav.zcore.location

import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a home that belongs to a player. */
class Home private constructor(id: EntityID<UUID>) : UUIDEntity(id), NamedLocation {

    companion object : UUIDEntityClass<Home>(Homes)

    /** The player this home belongs to. */
    var player by OfflinePlayer referencedOn Homes.player
        internal set

    /** The name of this home. */
    override var name: String by Homes.name
        internal set

    override var world: String by Homes.world
        internal set

    override var x: Double by Homes.x
        internal set

    override var y: Double by Homes.y
        internal set

    override var z: Double by Homes.z
        internal set

    override var pitch: Float by Homes.pitch
        internal set

    override var yaw: Float by Homes.yaw
        internal set

}