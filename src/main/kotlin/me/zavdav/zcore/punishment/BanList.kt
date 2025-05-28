package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Bans
import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.sql.and
import java.util.UUID

/** Represents a record of all issued bans. */
object BanList : PunishmentList<BanEntry, UUID>() {

    override val entries: Iterable<BanEntry> get() = BanEntry.all()

    /** Bans a [target] player and returns the [BanEntry] that was created from the arguments. */
    @JvmStatic
    fun addBan(target: OfflinePlayer, issuer: OfflinePlayer, duration: Long?, reason: String): BanEntry =
        addBan(target.uuid, issuer, duration, reason)

    /** Bans a [target] UUID and returns the [BanEntry] that was created from the arguments. */
    @JvmStatic
    fun addBan(target: UUID, issuer: OfflinePlayer, duration: Long?, reason: String): BanEntry {
        getActiveBan(target)?.active = false
        return BanEntry.new(target, issuer, duration, reason)
    }

    /**
     * Pardons a [target] player's most recent ban.
     * Returns `true` on success, `false` if this player is not banned.
     */
    @JvmStatic
    fun pardonBan(target: OfflinePlayer) = pardonBan(target.uuid)

    /**
     * Pardons a [target] UUID's most recent ban.
     * Returns `true` on success, `false` if this UUID is not banned.
     */
    @JvmStatic
    fun pardonBan(target: UUID): Boolean {
        val entry = getLastBan(target) ?: return false
        if (!entry.active) return false
        entry.active = false
        return true
    }

    /** Gets a [target] player's active ban, or `null` if this player is not banned. */
    @JvmStatic
    fun getActiveBan(target: OfflinePlayer): BanEntry? = getActiveBan(target.uuid)

    /** Gets a [target] UUID's active ban, or `null` if this UUID is not banned. */
    @JvmStatic
    fun getActiveBan(target: UUID): BanEntry? =
        BanEntry.find { Punishments.active and (Bans.target eq target) }.lastOrNull()

    /** Gets a [target] player's most recent ban, or `null` if this player has never been banned. */
    @JvmStatic
    fun getLastBan(target: OfflinePlayer): BanEntry? = getLastBan(target.uuid)

    /** Gets a [target] UUID's most recent ban, or `null` if this UUID has never been banned. */
    @JvmStatic
    fun getLastBan(target: UUID): BanEntry? =
        BanEntry.find { Bans.target eq target }.lastOrNull()

}