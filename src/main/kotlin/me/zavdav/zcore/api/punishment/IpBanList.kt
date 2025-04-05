package me.zavdav.zcore.api.punishment

import me.zavdav.zcore.api.user.OfflineUser
import java.net.Inet4Address

/** Represents a list of banned IPv4 addresses. */
interface IpBanList {

    /**
     * Adds a new entry with a [target] IP address to the list.
     * Returns the [IpBanEntry] that was created from the arguments.
     */
    fun addIpBan(target: Inet4Address, issuer: OfflineUser, duration: Long?, reason: String): IpBanEntry

    /** Removes the [target] IP address's most recent ban from the list. */
    fun removeIpBan(target: Inet4Address)

    /** Pardons the [target] IP address's most recent ban. */
    fun pardonIpBan(target: Inet4Address)

    /** Gets the [target] IP address's active ban, or null if the IP is not banned. */
    fun getActiveIpBan(target: Inet4Address): IpBanEntry?

}