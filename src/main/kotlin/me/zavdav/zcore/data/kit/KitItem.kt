package me.zavdav.zcore.data.kit

import me.zavdav.zcore.data.KitItems
import org.bukkit.Material
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID

/** Represents an item stack of a kit. */
class KitItem(id: EntityID<CompositeID>) : CompositeEntity(id) {

    internal companion object : CompositeEntityClass<KitItem>(KitItems) {
        fun new(
            kit: Kit,
            slot: Int,
            material: Material,
            data: Int,
            amount: Int
        ): KitItem =
            new(CompositeID {
                it[KitItems.kit] = kit.id
                it[KitItems.slot] = slot
            }) {
                this.material = material
                this.data = data
                this.amount = amount
            }
    }

    private var _slot by KitItems.slot

    /** The inventory slot the stack is in. */
    val slot: Int get() = _slot.value

    /** The item material. */
    var material: Material by KitItems.material
        private set

    /** The item data. */
    var data: Int by KitItems.data
        private set

    /** The amount of items in this stack. */
    var amount: Int by KitItems.amount
        private set

}