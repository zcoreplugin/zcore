package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.sql.and

/** Represents a record of all issued mutes. */
object MuteList : PunishmentList<MuteEntry, OfflineUser>() {

    override val entries: Iterable<MuteEntry> get() = MuteEntry.all()

    /** Mutes a [target] user and returns the [MuteEntry] that was created from the arguments. */
    @JvmStatic
    fun addMute(target: OfflineUser, issuer: OfflineUser, duration: Long?, reason: String): MuteEntry {
        getActiveMute(target)?.active = false
        return MuteEntry.new {
            this.target = target
            this.issuer = issuer
            this.timeIssued = System.currentTimeMillis()
            this.duration = duration
            this.reason = reason
        }
    }

    /**
     * Pardons a [target] user's most recent mute.
     * Returns `true` on success, `false` if this user is not muted.
     */
    @JvmStatic
    fun pardonMute(target: OfflineUser): Boolean {
        val entry = getLastMute(target) ?: return false
        if (!entry.active) return false
        entry.active = false
        return true
    }

    /** Gets a [target] user's active mute, or `null` if this user is not muted. */
    @JvmStatic
    fun getActiveMute(target: OfflineUser): MuteEntry? =
        MuteEntry.find { Punishments.active and (Mutes.target eq target.uuid) }.lastOrNull()

    /** Gets a [target] user's most recent mute, or `null` if this user has never been muted. */
    @JvmStatic
    fun getLastMute(target: OfflineUser): MuteEntry? =
        MuteEntry.find { Mutes.target eq target.uuid }.lastOrNull()

}