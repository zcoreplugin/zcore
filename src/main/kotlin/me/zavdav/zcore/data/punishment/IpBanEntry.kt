package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.user.OfflineUser
import java.net.Inet4Address
import java.util.UUID

/** Represents an IP ban entry with an IPv4 address as the target. */
class IpBanEntry(
    override val target: Inet4Address,
    override var issuer: OfflineUser,
    override var duration: Long?,
    override var reason: String
) : PunishmentEntry<Inet4Address>() {

    internal val _capturedUuids = mutableListOf<UUID>()

    /** A list of UUIDs of users that tried to join with the IP address. */
    val capturedUuids: List<UUID> get() = _capturedUuids

}