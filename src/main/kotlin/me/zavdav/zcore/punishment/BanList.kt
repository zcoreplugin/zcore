package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Bans
import me.zavdav.zcore.player.OfflinePlayer

/** Represents a record of all issued bans. */
object BanList {

    val entries: Iterable<Ban> get() = Ban.all().sortedBy { it.timeIssued }

    /**
     * Bans a player.
     *
     * @param target the target of the ban
     * @param issuer the player that issued the ban
     * @param duration the duration of the ban (permanent if `null`)
     * @param reason the reason for the ban
     * @return the [Ban] that was created
     */
    @JvmStatic
    fun addBan(target: OfflinePlayer, issuer: OfflinePlayer, duration: Long?, reason: String): Ban {
        pardonBan(target)
        return Ban.new {
            this.target = target
            this.issuer = issuer
            this.timeIssued = System.currentTimeMillis()
            this.duration = duration
            this.reason = reason
        }
    }

    /**
     * Pardons a player's currently active ban.
     *
     * @param target the target of the ban
     * @return `true` if the ban was pardoned, `false` if this player is not currently banned
     */
    @JvmStatic
    fun pardonBan(target: OfflinePlayer): Boolean {
        val ban = getActiveBan(target) ?: return false
        ban.pardoned = true
        return true
    }

    /**
     * Gets a player's currently active ban.
     *
     * @param target the target of the ban
     * @return the active ban, or `null` if this player is not currently banned
     */
    @JvmStatic
    fun getActiveBan(target: OfflinePlayer): Ban? =
        entries.lastOrNull { it.target == target && it.isActive }

    /**
     * Gets all bans of a player.
     *
     * @param target the target of the bans
     * @return a list of this player's bans
     */
    @JvmStatic
    fun getAllBans(target: OfflinePlayer): List<Ban> =
        Ban.find { Bans.target eq target.id }.toList()

}