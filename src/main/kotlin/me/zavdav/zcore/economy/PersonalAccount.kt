package me.zavdav.zcore.economy

import me.zavdav.zcore.data.PersonalAccounts
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.util.UUID

/** Represents a player's personal account. */
class PersonalAccount(id: EntityID<UUID>) : EconomyAccount(id) {

    internal companion object : UUIDEntityClass<PersonalAccount>(PersonalAccounts) {
        fun new(
            owner: OfflinePlayer,
            balance: BigDecimal = BigDecimal.ZERO,
            overdrawLimit: BigDecimal = BigDecimal.ZERO
        ): PersonalAccount {
            val base = EconomyAccount.new(owner, balance, overdrawLimit)
            return new(base.id.value) {}
        }
    }

}