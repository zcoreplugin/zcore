package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val itemCommand = command(
    "item",
    arrayOf("i"),
    "Gives you an item.",
    "/item <item> [<amount>]",
    "zcore.give"
) {
    materialArgument("material") {
        runs {
            val material: MaterialData by this
            doItem(material, Config.defaultGiveAmount)
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

    source.sendMessage(tl("command.item", itemStack.amount, material.displayName))
    source.inventory.addItem(itemStack)
}