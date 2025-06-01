package me.zavdav.zcore.economy

import me.zavdav.zcore.data.PersonalAccounts
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

/** Represents a player's personal account. */
class PersonalAccount internal constructor(id: EntityID<UUID>) : UUIDEntity(id), EconomyAccount {

    companion object : UUIDEntityClass<PersonalAccount>(PersonalAccounts)

    override var owner by OfflinePlayer referencedOn PersonalAccounts.owner

    private var _balance: BigDecimal by PersonalAccounts.balance

    override var balance: BigDecimal
        get() = _balance
        set(value) {
            if (value < -overdrawLimit) return
            _balance = value.setScale(10, RoundingMode.FLOOR)
        }

    override var overdrawLimit: BigDecimal by PersonalAccounts.overdrawLimit

}