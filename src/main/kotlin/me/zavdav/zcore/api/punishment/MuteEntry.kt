package me.zavdav.zcore.api.punishment

import me.zavdav.zcore.api.user.OfflineUser

/** Represents a mute entry with a user as the target. */
interface MuteEntry : PunishmentEntry<OfflineUser>