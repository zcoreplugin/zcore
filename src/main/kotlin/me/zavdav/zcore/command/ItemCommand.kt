package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val itemCommand = command(
    "item",
    arrayOf("i"),
    "Gives you an item",
    "zcore.item"
) {
    materialArgument("material") {
        runs {
            val material: MaterialData by this
            doItem(material, ZCoreConfig.getInt("command.give.default-amount"))
        }
        intArgument("amount") {
            runs {
                val material: MaterialData by this
                val amount: Int by this
                doItem(material, amount)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doItem(material: MaterialData, amount: Int) {
    val source = requirePlayer()
    val finalAmount = amount.coerceAtLeast(1)
    val itemStack = material.toItemStack(finalAmount)

    source.sendMessage(local("command.item", itemStack.amount, material.displayName, source.name))
    source.inventory.addItem(itemStack)
}