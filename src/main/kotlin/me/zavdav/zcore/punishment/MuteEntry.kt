package me.zavdav.zcore.punishment

import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a mute targeting a user. */
class MuteEntry(id: EntityID<UUID>) : PunishmentEntry<OfflineUser>(id) {

    internal companion object : UUIDEntityClass<MuteEntry>(Mutes) {
        fun new(target: OfflineUser, issuer: OfflineUser, duration: Long?, reason: String): MuteEntry {
            val base = new(issuer, duration, reason)
            return new(base.id.value) {
                this.target = target
            }
        }
    }

    override var target by OfflineUser referencedOn Mutes.target
        private set

}