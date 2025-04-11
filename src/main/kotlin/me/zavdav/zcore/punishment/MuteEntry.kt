package me.zavdav.zcore.punishment

import me.zavdav.zcore.user.OfflineUser

/** Represents a mute entry with a user as the target. */
class MuteEntry(
    override val target: OfflineUser,
    override var issuer: OfflineUser,
    override var duration: Long?,
    override var reason: String
) : PunishmentEntry<OfflineUser>()