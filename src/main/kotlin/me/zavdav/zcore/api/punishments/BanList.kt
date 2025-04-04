package me.zavdav.zcore.api.punishments

import java.util.UUID

/** Represents a list of banned UUIDs. */
interface BanList : PunishmentList<BanEntry, UUID>