package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.MuteEntries
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a mute targeting a player. */
class MuteEntry internal constructor(id: EntityID<UUID>) : UUIDEntity(id), PunishmentEntry<OfflinePlayer> {

    companion object : UUIDEntityClass<MuteEntry>(MuteEntries)

    override var target by OfflinePlayer referencedOn MuteEntries.target
        internal set

    override var issuer by OfflinePlayer referencedOn MuteEntries.issuer

    override var timeIssued: Long by MuteEntries.timeIssued
        internal set

    override var duration: Long? by MuteEntries.duration

    override var reason: String by MuteEntries.reason

    override var active: Boolean by MuteEntries.active

}