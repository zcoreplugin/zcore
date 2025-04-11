package me.zavdav.zcore.punishment

import me.zavdav.zcore.user.OfflineUser
import java.util.UUID

/** Represents a list of banned UUIDs. */
class BanList : PunishmentList<BanEntry, UUID>() {

    /**
     * Adds a new entry with a [target] user to the list.
     * Returns the [BanEntry] that was created from the arguments.
     */
    fun addBan(target: OfflineUser, issuer: OfflineUser, duration: Long?, reason: String): BanEntry =
        addBan(target.uuid, issuer, duration, reason)

    /**
     * Adds a new entry with a [target] UUID to the list.
     * Returns the [BanEntry] that was created from the arguments.
     */
    fun addBan(target: UUID, issuer: OfflineUser, duration: Long?, reason: String): BanEntry {
        getActiveBan(target)?.active = false
        val entry = BanEntry(target, issuer, duration, reason)
        _entries.add(entry)
        return entry
    }

    /** Removes the [target] user's most recent ban from the list. */
    fun removeBan(target: OfflineUser) = removeBan(target.uuid)

    /** Removes the [target] UUID's most recent ban from the list. */
    fun removeBan(target: UUID) = remove(target)

    /**
     * Pardons the [target] user's most recent ban.
     * Returns `false` if the user is not banned.
     */
    fun pardonBan(target: OfflineUser) = pardonBan(target.uuid)

    /**
     * Pardons the [target] UUID's most recent ban.
     * Returns `false` if the UUID is not banned.
     */
    fun pardonBan(target: UUID): Boolean = pardon(target)

    /** Gets the [target] user's active ban, or `null` if the user is not banned. */
    fun getActiveBan(target: OfflineUser): BanEntry? = getActiveBan(target.uuid)

    /** Gets the [target] UUID's active ban, or `null` if the UUID is not banned. */
    fun getActiveBan(target: UUID): BanEntry? = getActive(target)

    /** Gets the [target] user's most recent ban, or `null` if the user has never been banned. */
    fun getLastBan(target: OfflineUser): BanEntry? = getLastBan(target.uuid)

    /** Gets the [target] UUID's most recent ban, or `null` if the UUID has never been banned. */
    fun getLastBan(target: UUID): BanEntry? = getLast(target)

}