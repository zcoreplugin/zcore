package me.zavdav.zcore.api.punishments

import me.zavdav.zcore.api.user.OfflineUser

/** Represents a list of muted users. */
interface MuteList : PunishmentList<MuteEntry, OfflineUser>