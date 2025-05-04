package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a punishment with a target [T]. */
sealed class PunishmentEntry<T>(id: EntityID<UUID>) : UUIDEntity(id) {

    internal companion object : UUIDEntityClass<PunishmentEntry<*>>(Punishments) {
        fun new(issuer: OfflineUser, duration: Long?, reason: String): PunishmentEntry<*> =
            new {
                this.issuer = issuer
                this.timeIssued = System.currentTimeMillis()
                this.duration = duration
                this.reason = reason
            }
    }

    /** The target of this punishment. */
    abstract val target: T

    /** The user that issued this punishment. */
    var issuer by OfflineUser referencedOn Punishments.issuer

    /** The timestamp of when this punishment was issued. */
    var timeIssued: Long by Punishments.timeIssued
        private set

    /** The duration of this punishment in milliseconds, or `null` if this punishment is permanent. */
    var duration: Long? by Punishments.duration

    /** The timestamp of when this punishment expires, or `null` if this punishment is permanent. */
    var expiration: Long?
        get() = duration?.let { timeIssued + it }
        set(value) {
            duration = if (value != null) value - timeIssued else null
        }

    /** The reason for this punishment. */
    var reason: String by Punishments.reason

    /** Determines if this punishment is active. */
    var active: Boolean by Punishments.active

}