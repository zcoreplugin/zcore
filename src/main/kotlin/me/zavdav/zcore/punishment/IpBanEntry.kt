package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.IpBanEntries
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a ban targeting an IP address. */
class IpBanEntry private constructor(id: EntityID<UUID>) : UUIDEntity(id), PunishmentEntry<String> {

    companion object : UUIDEntityClass<IpBanEntry>(IpBanEntries)

    override var target: String by IpBanEntries.target
        internal set

    override var issuer by OfflinePlayer referencedOn IpBanEntries.issuer

    override var timeIssued: Long by IpBanEntries.timeIssued
        internal set

    override var duration: Long? by IpBanEntries.duration

    override var reason: String by IpBanEntries.reason

    override var active: Boolean by IpBanEntries.active

}