package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.user.OfflineUser
import org.jetbrains.exposed.sql.and
import java.net.Inet4Address

/** Represents a record of all issued IP bans. */
object IpBanList : PunishmentList<IpBanEntry, String>() {

    override val entries: Iterable<IpBanEntry> get() = IpBanEntry.all()

    /** Bans a [target] IP address and returns the [IpBanEntry] that was created from the arguments. */
    @JvmStatic
    fun addIpBan(target: Inet4Address, issuer: OfflineUser, duration: Long?, reason: String): IpBanEntry {
        getActiveIpBan(target)?.active = false
        return IpBanEntry.new(target, issuer, duration, reason)
    }

    /**
     * Pardons a [target] IP address's most recent ban.
     * Returns `true` on success, `false` if this IP address is not banned.
     */
    @JvmStatic
    fun pardonIpBan(target: Inet4Address): Boolean {
        val entry = getLastIpBan(target) ?: return false
        if (!entry.active) return false
        entry.active = false
        return true
    }

    /** Gets a [target] IP address's active ban, or `null` if this IP address is not banned. */
    @JvmStatic
    fun getActiveIpBan(target: Inet4Address): IpBanEntry? =
        IpBanEntry.find { Punishments.active and (IpBans.target eq target.hostAddress) }.lastOrNull()

    /** Gets a [target] IP address's most recent ban, or `null` if this IP address has never been banned. */
    @JvmStatic
    fun getLastIpBan(target: Inet4Address): IpBanEntry? =
        IpBanEntry.find { IpBans.target eq target.hostAddress }.lastOrNull()

}