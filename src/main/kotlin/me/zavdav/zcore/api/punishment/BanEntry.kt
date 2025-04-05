package me.zavdav.zcore.api.punishment

import me.zavdav.zcore.api.user.OfflineUser
import java.util.UUID

/** Represents a ban entry with a UUID as the target. */
interface BanEntry : PunishmentEntry<UUID> {

    /** The user who the banned UUID belongs to. Can be null if no such user exists. */
    val targetUser: OfflineUser

}