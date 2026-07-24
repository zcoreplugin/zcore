package me.zavdav.zcore.economy

import me.zavdav.zcore.data.PersonalAccounts
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
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
            if (value < BigDecimal.ZERO) return
            _balance = value.setScale(10, RoundingMode.DOWN)
        }

}