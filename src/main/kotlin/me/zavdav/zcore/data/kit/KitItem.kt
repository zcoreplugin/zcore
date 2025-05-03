package me.zavdav.zcore.data.kit

import me.zavdav.zcore.data.KitItems
import org.bukkit.Material
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID

/** Represents an item stack of a kit. */
class KitItem(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<KitItem>(KitItems)

    /** The inventory slot the stack is in. */
    val slot: Int by KitItems.slot

    /** The item material. */
    val material: Material by KitItems.material

    /** The item data. */
    val data: Int by KitItems.data

    /** The amount of items in this stack. */
    val amount: Int by KitItems.amount

}