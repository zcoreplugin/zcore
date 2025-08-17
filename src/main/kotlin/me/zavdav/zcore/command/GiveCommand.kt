package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val giveCommand = command(
    "give",
    "Gives a player an item",
    "zcore.give"
) {
    playerArgument("player") {
        materialArgument("material") {
            runs {
                val player: CorePlayer by this
                val material: MaterialData by this
                doGive(player, material, ZCoreConfig.getInt("command.give.default-amount"))
            }
            intArgument("amount") {
                runs {
                    val player: CorePlayer by this
                    val material: MaterialData by this
                    val amount: Int by this
                    doGive(player, material, amount)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doGive(target: CorePlayer, material: MaterialData, amount: Int) {
    val source = this.source
    val finalAmount = amount.coerceAtLeast(1)
    val itemStack = material.toItemStack(finalAmount)

    source.sendMessage(local("command.give", itemStack.amount, material.displayName, target.name))
    target.inventory.addItem(itemStack)
}