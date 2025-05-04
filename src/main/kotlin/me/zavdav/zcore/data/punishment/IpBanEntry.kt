package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.IpBanUuids
import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID
import java.net.Inet4Address
import java.util.UUID

/** Represents a ban targeting an IP address. */
class IpBanEntry(id: EntityID<UUID>) : PunishmentEntry<String>(id) {

    internal companion object : UUIDEntityClass<IpBanEntry>(IpBans) {
        fun new(target: Inet4Address, issuer: OfflineUser, duration: Long?, reason: String): IpBanEntry {
            val base = new(issuer, duration, reason)
            return new(base.id.value) {
                this.target = target.hostAddress
            }
        }
    }

    override var target: String by IpBans.target
        private set

    /** The UUIDs of users that tried to join with this IP address. */
    val capturedUuids by CapturedUuid via IpBanUuids

    class CapturedUuid(id: EntityID<CompositeID>) : CompositeEntity(id) {
        internal companion object : CompositeEntityClass<CapturedUuid>(IpBanUuids)

        val value: UUID by IpBanUuids.uuid

    }

}