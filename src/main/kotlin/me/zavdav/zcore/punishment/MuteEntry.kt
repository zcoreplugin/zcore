package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a mute targeting a player. */
class MuteEntry(id: EntityID<UUID>) : PunishmentEntry<OfflinePlayer>(id) {

    internal companion object : UUIDEntityClass<MuteEntry>(Mutes) {
        fun new(target: OfflinePlayer, issuer: OfflinePlayer, duration: Long?, reason: String): MuteEntry {
            val base = new(issuer, duration, reason)
            return new(base.id.value) {
                this.target = target
            }
        }
    }

    override var target by OfflinePlayer referencedOn Mutes.target
        private set

}