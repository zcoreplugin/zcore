package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a ban targeting a range of IP addresses. */
class IpBan internal constructor(id: EntityID<UUID>) : UUIDEntity(id), Punishment<IpAddressRange> {

    companion object : UUIDEntityClass<IpBan>(IpBans)

    override var target: IpAddressRange by IpBans.target
        internal set

    override var issuer by OfflinePlayer referencedOn IpBans.issuer
        internal set

    override var timeIssued: Long by IpBans.timeIssued
        internal set

    override var duration: Long? by IpBans.duration

    override var reason: String by IpBans.reason

    override var pardoned: Boolean by IpBans.pardoned

}