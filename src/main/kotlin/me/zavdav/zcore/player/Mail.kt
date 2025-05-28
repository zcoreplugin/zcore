package me.zavdav.zcore.player

import me.zavdav.zcore.data.Mails
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents mail that a player sent to another player. */
class Mail(id: EntityID<UUID>) : UUIDEntity(id) {

    internal companion object : UUIDEntityClass<Mail>(Mails) {
        fun new(sender: OfflinePlayer, recipient: OfflinePlayer, message: String): Mail =
            new {
                this.sender = sender
                this.recipient = recipient
                this.message = message
            }
    }

    /** The player that sent this mail. */
    var sender by OfflinePlayer referencedOn Mails.sender
        private set

    /** The player that received this mail. */
    var recipient by OfflinePlayer referencedOn Mails.recipient
        private set

    /** The content of this mail. */
    var message: String by Mails.message
        private set

}