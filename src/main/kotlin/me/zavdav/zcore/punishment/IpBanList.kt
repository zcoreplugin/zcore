package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.player.OfflinePlayer

/** Represents a record of all issued IP bans. */
object IpBanList {

    val entries: Iterable<IpBan> get() = IpBan.all().sortedBy { it.timeIssued }

    /**
     * Bans a range of IP addresses.
     *
     * @param target the target of the ban
     * @param issuer the player that issued the ban
     * @param duration the duration of the ban (permanent if `null`)
     * @param reason the reason for the ban
     * @return the [IpBan] that was created
     */
    @JvmStatic
    fun addIpBan(target: IpAddressRange, issuer: OfflinePlayer, duration: Long?, reason: String): IpBan {
        pardonIpBan(target)
        return IpBan.new {
            this.target = target
            this.issuer = issuer
            this.timeIssued = System.currentTimeMillis()
            this.duration = duration
            this.reason = reason
        }
    }

    /**
     * Pardons the currently active ban of a range of IP addresses.
     *
     * @param target the target of the ban
     * @return `true` if the ban was pardoned, `false` if this address range is not currently banned
     */
    @JvmStatic
    fun pardonIpBan(target: IpAddressRange): Boolean {
        val ban = getActiveIpBan(target) ?: return false
        ban.pardoned = true
        return true
    }

    /**
     * Gets the currently active ban of a range of IP addresses.
     *
     * @param target the target of the ban
     * @return the active ban, or `null` if this address range is not currently banned
     */
    @JvmStatic
    fun getActiveIpBan(target: IpAddressRange): IpBan? =
        entries.lastOrNull { it.target == target && it.isActive }

    /**
     * Gets all bans of a range of IP addresses.
     *
     * @param target the target of the bans
     * @return a list of the bans of this address range
     */
    @JvmStatic
    fun getAllIpBans(target: IpAddressRange): List<IpBan> =
        IpBan.find { IpBans.target eq target }.toList()

}