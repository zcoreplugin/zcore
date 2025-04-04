package me.zavdav.zcore.api.punishments

import java.net.Inet4Address

/** Represents an IP ban entry with an IPv4 address as the target. */
interface IpBanEntry : PunishmentEntry<Inet4Address>