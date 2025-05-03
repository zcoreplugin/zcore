package me.zavdav.zcore.data.location

import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a home that belongs to a user. */
class Home(id: EntityID<UUID>) : Location(id) {
    companion object : UUIDEntityClass<Home>(Homes)

    /** The user this home belongs to. */
    var user by OfflineUser referencedOn Homes.user
        internal set

    /** The name of this home. */
    var name: String by Homes.name
        internal set

}