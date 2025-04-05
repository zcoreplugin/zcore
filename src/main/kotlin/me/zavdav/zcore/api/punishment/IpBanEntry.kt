package me.zavdav.zcore.api.punishment

import java.net.Inet4Address
import java.util.UUID

/** Represents an IP ban entry with an IPv4 address as the target. */
interface IpBanEntry : PunishmentEntry<Inet4Address> {

    /** A list of UUIDs of users that tried to join with the IP address. */
    val capturedUuids: List<UUID>

}