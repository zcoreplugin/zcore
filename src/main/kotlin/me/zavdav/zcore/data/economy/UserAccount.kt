package me.zavdav.zcore.data.economy

import me.zavdav.zcore.data.Accounts
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a user's personal account. */
class UserAccount(id: EntityID<UUID>) : EconomyAccount(id) {
    companion object : UUIDEntityClass<UserAccount>(Accounts)
}