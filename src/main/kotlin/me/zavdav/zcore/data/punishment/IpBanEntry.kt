package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.IpBanUuids
import me.zavdav.zcore.data.IpBans
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a ban targeting an IP address. */
class IpBanEntry(id: EntityID<UUID>) : PunishmentEntry<String>(id) {
    companion object : UUIDEntityClass<IpBanEntry>(IpBans)

    override var target: String by IpBans.target
        internal set

    /** The UUIDs of users that tried to join with this IP address. */
    val capturedUuids by CapturedUuid via IpBanUuids

    class CapturedUuid(id: EntityID<CompositeID>) : CompositeEntity(id) {
        companion object : CompositeEntityClass<CapturedUuid>(IpBanUuids)

        val value: UUID by IpBanUuids.uuid

    }

}