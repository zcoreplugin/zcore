package me.zavdav.zcore.data.kit

import me.zavdav.zcore.data.KitItems
import me.zavdav.zcore.data.Kits
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.util.UUID

/** Represents a kit that users can equip. */
class Kit(id: EntityID<UUID>) : UUIDEntity(id) {

    internal companion object : UUIDEntityClass<Kit>(Kits) {
        fun new(
            name: String,
            items: Map<Int, ItemStack>,
            cost: BigDecimal = BigDecimal.ZERO,
            cooldown: Long = 0
        ): Kit {
            val kit = new {
                this.name = name
                this.cost = cost
                this.cooldown = cooldown
            }
            items.forEach { (slot, item) ->
                KitItem.new(kit, slot, item.type, item.durability.toInt(), item.amount)
            }
            return kit
        }
    }

    /** The name of this kit. */
    var name: String by Kits.name

    /** The items of this kit. */
    val items by KitItem referrersOn KitItems.kit

    /** The cost to equip this kit. */
    var cost: BigDecimal by Kits.cost

    /** The cooldown in milliseconds between equipping this kit. */
    var cooldown: Long by Kits.cooldown

}