package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.net.Inet4Address
import java.util.UUID

/** Represents a ban targeting an IP address. */
class IpBan internal constructor(id: EntityID<UUID>) : UUIDEntity(id), Punishment<Inet4Address> {

    companion object : UUIDEntityClass<IpBan>(IpBans)

    override var target: Inet4Address by IpBans.target
        internal set

    override var issuer by OfflinePlayer optionalReferencedOn IpBans.issuer
        internal set

    override var timeIssued: Long by IpBans.timeIssued
        internal set

    override var duration: Long? by IpBans.duration

    override var reason: String by IpBans.reason

    override var pardoned: Boolean by IpBans.pardoned

}