package me.zavdav.zcore.punishment

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.BanEntries
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a ban targeting a UUID. */
class BanEntry internal constructor(id: EntityID<UUID>) : UUIDEntity(id), PunishmentEntry<UUID> {

    companion object : UUIDEntityClass<BanEntry>(BanEntries)

    override var target: UUID by BanEntries.target
        internal set

    override var issuer by OfflinePlayer referencedOn BanEntries.issuer

    override var timeIssued: Long by BanEntries.timeIssued
        internal set

    override var duration: Long? by BanEntries.duration

    override var reason: String by BanEntries.reason

    override var active: Boolean by BanEntries.active

    /** The player with this UUID. Can be `null` if no such player exists. */
    val targetPlayer: OfflinePlayer?
        get() = ZCore.getOfflinePlayer(target)

}