package me.zavdav.zcore.api.punishments

import java.util.UUID

/** Represents a ban entry with a UUID as the target. */
interface BanEntry : PunishmentEntry<UUID>