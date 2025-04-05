package me.zavdav.zcore.api.punishment

import me.zavdav.zcore.api.user.OfflineUser
import java.util.UUID

/** Represents a list of banned UUIDs. */
interface BanList {

    /**
     * Adds a new entry with a [target] user to the list.
     * Returns the [BanEntry] that was created from the arguments.
     */
    fun addBan(target: OfflineUser, issuer: OfflineUser, duration: Long?, reason: String): BanEntry

    /**
     * Adds a new entry with a [target] UUID to the list.
     * Returns the [BanEntry] that was created from the arguments.
     */
    fun addBan(target: UUID, issuer: OfflineUser, duration: Long?, reason: String): BanEntry

    /** Removes the [target] user's most recent ban from the list. */
    fun removeBan(target: OfflineUser)

    /** Removes the [target] UUID's most recent ban from the list. */
    fun removeBan(target: UUID)

    /** Pardons the [target] user's most recent ban. */
    fun pardonBan(target: OfflineUser)

    /** Pardons the [target] UUID's most recent ban. */
    fun pardonBan(target: UUID)

    /** Gets the [target] user's active ban, or null if the user is not banned. */
    fun getActiveBan(target: OfflineUser): BanEntry?

    /** Gets the [target] UUID's active ban, or null if the UUID is not banned. */
    fun getActiveBan(target: UUID): BanEntry?

}