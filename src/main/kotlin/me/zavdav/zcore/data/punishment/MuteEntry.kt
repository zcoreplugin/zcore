package me.zavdav.zcore.data.punishment

import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a mute targeting a user. */
class MuteEntry(id: EntityID<UUID>) : PunishmentEntry<OfflineUser>(id) {
    companion object : UUIDEntityClass<MuteEntry>(Mutes)

    override var target by OfflineUser referencedOn Mutes.target
        internal set

}