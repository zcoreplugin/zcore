package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Bans
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a ban targeting a player. */
class Ban internal constructor(id: EntityID<UUID>) : UUIDEntity(id), Punishment<OfflinePlayer> {

    companion object : UUIDEntityClass<Ban>(Bans)

    override var target by OfflinePlayer referencedOn Bans.target
        internal set

    override var issuer by OfflinePlayer referencedOn Bans.issuer
        internal set

    override var timeIssued: Long by Bans.timeIssued
        internal set

    override var duration: Long? by Bans.duration

    override var reason: String by Bans.reason

    override var pardoned: Boolean by Bans.pardoned

}