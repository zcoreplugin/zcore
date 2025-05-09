package me.zavdav.zcore.user

import me.zavdav.zcore.data.Mails
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents mail that a user sent to another user. */
class Mail(id: EntityID<UUID>) : UUIDEntity(id) {

    internal companion object : UUIDEntityClass<Mail>(Mails) {
        fun new(sender: OfflineUser, recipient: OfflineUser, message: String): Mail =
            new {
                this.sender = sender
                this.recipient = recipient
                this.message = message
            }
    }

    /** The user that sent this mail. */
    var sender by OfflineUser referencedOn Mails.sender
        private set

    /** The user that received this mail. */
    var recipient by OfflineUser referencedOn Mails.recipient
        private set

    /** The content of this mail. */
    var message: String by Mails.message
        private set

}