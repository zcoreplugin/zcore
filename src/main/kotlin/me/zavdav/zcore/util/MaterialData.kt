package me.zavdav.zcore.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

internal data class MaterialData(val material: Material, val data: Short) {

    val displayName: String
        get() = runCatching { tl("material.${material.id}.$data") }
            .recoverCatching { tl("material.${material.id}.0") }
            .recoverCatching { tl("material.${material.id}") }
            .getOrElse { tl("material.unknown") }

    fun toItemStack(amount: Int): ItemStack =
        ItemStack(material, amount.coerceAtLeast(1), data)

}