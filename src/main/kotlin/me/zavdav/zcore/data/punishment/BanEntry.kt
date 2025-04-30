package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.user.OfflineUser
import java.util.UUID

/** Represents a ban entry with a UUID as the target. */
class BanEntry(
    override val target: UUID,
    override var issuer: OfflineUser,
    override var duration: Long?,
    override var reason: String
) : PunishmentEntry<UUID>() {

    /** The user who the banned UUID belongs to. Can be `null` if no such user exists. */
    val targetUser: OfflineUser?
        get() = ZCore.getOfflineUser(target)

}