package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.user.OfflineUser
import java.net.Inet4Address

/** Represents a list of banned IPv4 addresses. */
class IpBanList : PunishmentList<IpBanEntry, Inet4Address>() {

    /**
     * Adds a new entry with a [target] IP address to the list.
     * Returns the [IpBanEntry] that was created from the arguments.
     */
    fun addIpBan(target: Inet4Address, issuer: OfflineUser, duration: Long?, reason: String): IpBanEntry {
        getActiveIpBan(target)?.active = false
        val entry = IpBanEntry(target, issuer, duration, reason)
        _entries.add(entry)
        return entry
    }

    /** Removes the [target] IP address's most recent ban from the list. */
    fun removeIpBan(target: Inet4Address) = remove(target)

    /**
     * Pardons the [target] IP address's most recent ban.
     * Returns `false` if the IP is not banned.
     */
    fun pardonIpBan(target: Inet4Address): Boolean = pardon(target)

    /** Gets the [target] IP address's active ban, or `null` if the IP is not banned. */
    fun getActiveIpBan(target: Inet4Address): IpBanEntry? = getActive(target)

    /** Gets the [target] IP address's most recent ban, or `null` if the IP has never been banned. */
    fun getLastIpBan(target: Inet4Address): IpBanEntry? = getLast(target)

}