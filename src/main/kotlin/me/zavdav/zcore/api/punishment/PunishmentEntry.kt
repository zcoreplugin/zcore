package me.zavdav.zcore.api.punishment

import me.zavdav.zcore.api.user.OfflineUser

/** Represents an entry of a punishment list with a target [T]. */
interface PunishmentEntry<T> {

    /** The target of the punishment. */
    val target: T

    /** The user that issued the punishment. */
    var issuer: OfflineUser

    /** The epoch millisecond of when the punishment was issued. */
    val timeIssued: Long

    /** The duration of the punishment in milliseconds, or null if the punishment is permanent. */
    var duration: Long?

    /** The epoch millisecond of when the punishment expires, or null if the punishment is permanent. */
    var expiration: Long?

    /** The reason for the punishment. */
    var reason: String

    /** Determines if the punishment is active. */
    var active: Boolean

}