package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.player.OfflinePlayer
import java.net.Inet4Address

/** Represents a record of all issued IP bans. */
object IpBanList {

    val entries: Iterable<IpBan> get() = IpBan.all().sortedBy { it.timeIssued }

    /**
     * Adds a new ban targeting an IP address.
     * If the IP address is already banned, the current ban will be overwritten.
     *
     * @param target the target of the ban
     * @param issuer the player that issued the ban (`null` means issued by console)
     * @param duration the duration of the ban (permanent if `null`)
     * @param reason the reason for the ban
     * @return the [IpBan] that was created
     */
    @JvmStatic
    fun addBan(target: Inet4Address, issuer: OfflinePlayer?, duration: Long?, reason: String): IpBan {
        getActiveBan(target)?.delete()
        return IpBan.new {
            this.target = target
            this.issuer = issuer
            this.timeIssued = System.currentTimeMillis()
            this.duration = duration
            this.reason = reason
        }
    }

    /**
     * Pardons the currently active ban of an IP address.
     *
     * @param target the target of the ban
     * @return `true` if the ban was pardoned, `false` if this IP address is not currently banned
     */
    @JvmStatic
    fun pardonBan(target: Inet4Address): Boolean {
        val ban = getActiveBan(target) ?: return false
        ban.pardoned = true
        return true
    }

    /**
     * Gets the currently active ban of an IP address.
     *
     * @param target the target of the ban
     * @return the active ban, or `null` if this IP address is not currently banned
     */
    @JvmStatic
    fun getActiveBan(target: Inet4Address): IpBan? =
        entries.lastOrNull { it.target == target && it.isActive }

    /**
     * Gets all bans of an IP address.
     *
     * @param target the target of the bans
     * @return a list of this IP address's bans
     */
    @JvmStatic
    fun getAllBans(target: Inet4Address): List<IpBan> =
        IpBan.find { IpBans.target eq target }.toList()

}