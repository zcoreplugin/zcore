package me.zavdav.zcore.api.punishments

import me.zavdav.zcore.api.user.OfflineUser

/** Represents an entry of a [PunishmentList] with a target of type [T]. */
interface PunishmentEntry<T> {

    /** The target of the punishment. */
    val target: T

    /** The user that issued the punishment. Can be null if it was issued by the console. */
    var issuer: OfflineUser?

    /** The epoch millisecond of when the punishment was issued. */
    val issued: Long

    /** The duration of the punishment in milliseconds. */
    var duration: Long

    /** Determines if the punishment was pardoned. */
    var pardoned: Boolean

}