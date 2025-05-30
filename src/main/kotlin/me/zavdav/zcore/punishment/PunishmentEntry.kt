package me.zavdav.zcore.punishment

import me.zavdav.zcore.player.OfflinePlayer

/** Represents a punishment with a target [T]. */
sealed interface PunishmentEntry<T> {

    /** The target of this punishment. */
    val target: T

    /** The player that issued this punishment. */
    var issuer: OfflinePlayer

    /** The timestamp of when this punishment was issued. */
    val timeIssued: Long

    /** The duration of this punishment in milliseconds, or `null` if this punishment is permanent. */
    var duration: Long?

    /** The timestamp of when this punishment expires, or `null` if this punishment is permanent. */
    var expiration: Long?
        get() = duration?.let { timeIssued + it }
        set(value) {
            duration = if (value != null) value - timeIssued else null
        }

    /** The reason for this punishment. */
    var reason: String

    /** Determines if this punishment is active. */
    var active: Boolean

}