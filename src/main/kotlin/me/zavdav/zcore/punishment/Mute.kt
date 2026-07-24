package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

/** Represents a mute targeting a player. */
class Mute internal constructor(id: EntityID<UUID>) : UUIDEntity(id), Punishment<OfflinePlayer> {

    companion object : UUIDEntityClass<Mute>(Mutes)

    override var target by OfflinePlayer referencedOn Mutes.target
        internal set

    override var issuer by OfflinePlayer optionalReferencedOn Mutes.issuer
        internal set

    override var timeIssued: Long by Mutes.timeIssued
        internal set

    override var duration: Long? by Mutes.duration

    override var reason: String by Mutes.reason

    override var pardoned: Boolean by Mutes.pardoned

}