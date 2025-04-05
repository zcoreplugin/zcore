package me.zavdav.zcore.api.punishment

import me.zavdav.zcore.api.user.OfflineUser

/** Represents a list of muted users. */
interface MuteList {

    /**
     * Adds a new entry with a [target] user to the list.
     * Returns the [MuteEntry] that was created from the arguments.
     */
    fun addMute(target: OfflineUser, issuer: OfflineUser, duration: Long?, reason: String): MuteEntry

    /** Removes the [target] user's most recent mute from the list. */
    fun removeMute(target: OfflineUser)

    /** Pardons the [target] user's most recent mute. */
    fun pardonMute(target: OfflineUser)

    /** Gets the [target] user's active mute, or null if the user is not muted. */
    fun getActiveMute(target: OfflineUser): MuteEntry?

}