package me.zavdav.zcore.kit

import me.zavdav.zcore.data.KitItems
import org.bukkit.Material
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents an item stack of a kit. */
class KitItem private constructor(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<KitItem>(KitItems)

    /** The kit this stack belongs to. */
    var kit by Kit referencedOn KitItems.kit
        internal set

    /** The inventory slot this stack is in. */
    var slot: Int by KitItems.slot
        internal set

    /** The item material. */
    var material: Material by KitItems.material
        internal set

    /** The item data. */
    var data: Short by KitItems.data
        internal set

    /** The amount of items in this stack. */
    var amount: Int by KitItems.amount
        internal set

}