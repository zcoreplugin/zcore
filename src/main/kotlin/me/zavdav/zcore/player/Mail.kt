package me.zavdav.zcore.player

import me.zavdav.zcore.data.Mails
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents mail that a player sent to another player. */
class Mail private constructor(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<Mail>(Mails)

    /** The player that sent this mail. */
    var sender by OfflinePlayer referencedOn Mails.sender
        internal set

    /** The player that received this mail. */
    var recipient by OfflinePlayer referencedOn Mails.recipient
        internal set

    /** The content of this mail. */
    var message: String by Mails.message
        internal set

}