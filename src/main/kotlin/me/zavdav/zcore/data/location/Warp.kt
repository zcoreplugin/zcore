package me.zavdav.zcore.data.location

import me.zavdav.zcore.data.Warps
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a location that users can warp to. */
class Warp(id: EntityID<UUID>) : Location(id) {
    companion object : UUIDEntityClass<Warp>(Warps)

    /** The name of this warp. */
    var name: String by Warps.name
        internal set

}