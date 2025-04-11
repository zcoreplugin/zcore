package me.zavdav.zcore.punishment

import me.zavdav.zcore.user.OfflineUser

/** Represents a list of muted users. */
class MuteList : PunishmentList<MuteEntry, OfflineUser>() {

    /**
     * Adds a new entry with a [target] user to the list.
     * Returns the [MuteEntry] that was created from the arguments.
     */
    fun addMute(target: OfflineUser, issuer: OfflineUser, duration: Long?, reason: String): MuteEntry {
        getActiveMute(target)?.active = false
        val entry = MuteEntry(target, issuer, duration, reason)
        _entries.add(entry)
        return entry
    }

    /** Removes the [target] user's most recent mute from the list. */
    fun removeMute(target: OfflineUser) = remove(target)

    /**
     * Pardons the [target] user's most recent mute.
     * Returns `false` if the user is not muted.
     */
    fun pardonMute(target: OfflineUser): Boolean = pardon(target)

    /** Gets the [target] user's active mute, or `null` if the user is not muted. */
    fun getActiveMute(target: OfflineUser): MuteEntry? = getActive(target)

    /** Gets the [target] user's most recent mute, or `null` if the user has never been muted. */
    fun getLastMute(target: OfflineUser): MuteEntry? = getLast(target)

}