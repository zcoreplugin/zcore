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
class PersonalAccount internal constructor(id: EntityID<UUID>) : UUIDEntity(id), Account {

    companion object : UUIDEntityClass<PersonalAccount>(PersonalAccounts)

    override var owner by OfflinePlayer referencedOn PersonalAccounts.owner
        internal set

    private var _balance: BigDecimal by PersonalAccounts.balance

    override var balance: BigDecimal
        get() = _balance
        set(value) {
            if (value < -overdrawLimit) return
            _balance = value.setScale(10, RoundingMode.DOWN)
        }

    private var _overdrawLimit: BigDecimal by PersonalAccounts.overdrawLimit

    override var overdrawLimit: BigDecimal
        get() = _overdrawLimit
        set(value) {
            if (value < BigDecimal.ZERO)
                throw IllegalArgumentException("Invalid amount: $value")

            _overdrawLimit = value
        }

}