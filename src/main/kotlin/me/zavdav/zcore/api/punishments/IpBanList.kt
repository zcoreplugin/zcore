package me.zavdav.zcore.api.punishments

import java.net.Inet4Address

/** Represents a list of banned IPv4 addresses. */
interface IpBanList : PunishmentList<IpBanEntry, Inet4Address>