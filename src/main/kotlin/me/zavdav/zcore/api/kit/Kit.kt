package me.zavdav.zcore.api.kit

import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

/** Represents a kit that users can equip. */
interface Kit {

    /** The name of the kit. */
    val name: String

    /** A map of inventory indexes together with the respective item stacks. */
    val items: Map<Int, ItemStack>

    /** The cost to equip the kit. */
    val cost: BigDecimal

    /** The cooldown in milliseconds between equipping the kit. */
    val cooldown: Long

}