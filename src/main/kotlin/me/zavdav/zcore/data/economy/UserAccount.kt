package me.zavdav.zcore.data.economy

import me.zavdav.zcore.data.UserAccounts
import me.zavdav.zcore.data.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.util.UUID

/** Represents a user's personal account. */
class UserAccount(id: EntityID<UUID>) : EconomyAccount(id) {

    internal companion object : UUIDEntityClass<UserAccount>(UserAccounts) {
        fun new(
            owner: OfflineUser,
            balance: BigDecimal = BigDecimal.ZERO,
            overdrawLimit: BigDecimal = BigDecimal.ZERO
        ): UserAccount {
            val base = EconomyAccount.new(owner, balance, overdrawLimit)
            return new(base.id.value) {}
        }
    }

}