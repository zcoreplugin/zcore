package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.MuteEntries
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.sql.and

/** Represents a record of all issued mutes. */
object MuteList : PunishmentList<MuteEntry, OfflinePlayer> {

    override val entries: Iterable<MuteEntry> get() = MuteEntry.all()

    /** Mutes a [target] player and returns the [MuteEntry] that was created from the arguments. */
    @JvmStatic
    fun addMute(target: OfflinePlayer, issuer: OfflinePlayer, duration: Long?, reason: String): MuteEntry {
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
     * Pardons a [target] player's most recent mute.
     * Returns `true` on success, `false` if this player is not muted.
     */
    @JvmStatic
    fun pardonMute(target: OfflinePlayer): Boolean {
        val entry = getLastMute(target) ?: return false
        if (!entry.active) return false
        entry.active = false
        return true
    }

    /** Gets a [target] player's active mute, or `null` if this player is not muted. */
    @JvmStatic
    fun getActiveMute(target: OfflinePlayer): MuteEntry? =
        MuteEntry.find { MuteEntries.active and (MuteEntries.target eq target.uuid) }.lastOrNull()

    /** Gets a [target] player's most recent mute, or `null` if this player has never been muted. */
    @JvmStatic
    fun getLastMute(target: OfflinePlayer): MuteEntry? =
        MuteEntry.find { MuteEntries.target eq target.uuid }.lastOrNull()

}