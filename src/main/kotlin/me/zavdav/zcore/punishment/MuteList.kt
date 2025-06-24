package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.player.OfflinePlayer

/** Represents a record of all issued mutes. */
object MuteList {

    val entries: Iterable<Mute> get() = Mute.all().sortedBy { it.timeIssued }

    /**
     * Mutes a player.
     *
     * @param target the target of the mute
     * @param issuer the player that issued the mute (`null` means issued by console)
     * @param duration the duration of the mute (permanent if `null`)
     * @param reason the reason for the mute
     * @return the [Mute] that was created
     */
    @JvmStatic
    fun addMute(target: OfflinePlayer, issuer: OfflinePlayer?, duration: Long?, reason: String): Mute {
        pardonMute(target)
        return Mute.new {
            this.target = target
            this.issuer = issuer
            this.timeIssued = System.currentTimeMillis()
            this.duration = duration
            this.reason = reason
        }
    }

    /**
     * Pardons a player's currently active mute.
     *
     * @param target the target of the mute
     * @return `true` if the mute was pardoned, `false` if this player is not currently muted
     */
    @JvmStatic
    fun pardonMute(target: OfflinePlayer): Boolean {
        val mute = getActiveMute(target) ?: return false
        mute.pardoned = true
        return true
    }

    /**
     * Gets a player's currently active mute.
     *
     * @param target the target of the mute
     * @return the active mute, or `null` if this player is not currently muted
     */
    @JvmStatic
    fun getActiveMute(target: OfflinePlayer): Mute? =
        entries.lastOrNull { it.target == target && it.isActive }

    /**
     * Gets all mutes of a player.
     *
     * @param target the target of the mutes
     * @return a list of this player's mutes
     */
    @JvmStatic
    fun getAllMutes(target: OfflinePlayer): List<Mute> =
        Mute.find { Mutes.target eq target.id }.toList()

}