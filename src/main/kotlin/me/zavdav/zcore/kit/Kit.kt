package me.zavdav.zcore.kit

import me.zavdav.zcore.data.KitItems
import me.zavdav.zcore.data.Kits
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.util.UUID

/** Represents a kit that players can equip. */
class Kit internal constructor(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<Kit>(Kits)

    /** The name of this kit. */
    var name: String by Kits.name

    /** The items of this kit. */
    val items by KitItem referrersOn KitItems.kit

    /** The cost to equip this kit. */
    var cost: BigDecimal by Kits.cost

    /** The cooldown in milliseconds between equipping this kit. */
    var cooldown: Long by Kits.cooldown

}