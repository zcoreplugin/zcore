package me.zavdav.zcore.punishment

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.Bans
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a ban targeting a UUID. */
class BanEntry(id: EntityID<UUID>) : PunishmentEntry<UUID>(id) {

    internal companion object : UUIDEntityClass<BanEntry>(Bans) {
        fun new(target: UUID, issuer: OfflinePlayer, duration: Long?, reason: String): BanEntry {
            val base = new(issuer, duration, reason)
            return new(base.id.value) {
                this.target = target
            }
        }
    }

    override var target: UUID by Bans.target
        private set

    /** The player with this UUID. Can be `null` if no such player exists. */
    val targetPlayer: OfflinePlayer?
        get() = ZCore.getOfflinePlayer(target)

}