package me.zavdav.zcore.punishment

import me.zavdav.zcore.player.OfflinePlayer

/** Represents a punishment with a target. */
sealed interface Punishment<T> {

    /** The target of this punishment. */
    val target: T

    /** The player that issued this punishment. */
    val issuer: OfflinePlayer

    /** The timestamp of when this punishment was issued. */
    val timeIssued: Long

    /** The duration of this punishment in milliseconds (permanent if `null`). */
    var duration: Long?

    /** The timestamp of when this punishment expires (permanent if `null`). */
    var expiration: Long?
        get() = duration?.let { timeIssued + it }
        set(value) {
            duration = if (value != null) value - timeIssued else null
        }

    /** The reason for this punishment. */
    var reason: String

    /** Determines if this punishment has been pardoned. */
    var pardoned: Boolean

    /** @return `true` if this punishment is active */
    val isActive: Boolean
        get() {
            if (pardoned) return false
            val expiration = this.expiration
            if (expiration == null) return true
            return System.currentTimeMillis() < expiration
        }

}