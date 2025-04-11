package me.zavdav.zcore.punishment

import me.zavdav.zcore.user.OfflineUser

/** Represents an entry of a punishment list with a target [T]. */
sealed class PunishmentEntry<T> {

    /** The target of the punishment. */
    abstract val target: T

    /** The user that issued the punishment. */
    abstract var issuer: OfflineUser

    /** The epoch millisecond of when the punishment was issued. */
    val timeIssued: Long = System.currentTimeMillis()

    /** The duration of the punishment in milliseconds, or null if the punishment is permanent. */
    abstract var duration: Long?

    /** The epoch millisecond of when the punishment expires, or null if the punishment is permanent. */
    var expiration: Long?
        get() = duration?.let { timeIssued + it }
        set(value) {
            duration = if (value != null) value - timeIssued else null
        }

    /** The reason for the punishment. */
    abstract var reason: String

    /** Determines if the punishment is active. */
    var active: Boolean = true

}