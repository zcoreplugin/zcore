package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import net.minecraft.server.ContainerPlayer
import net.minecraft.server.ItemStack
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

internal val clearCommand = command(
    "clear",
    "Clears your inventory.",
    "/clear",
    "zcore.clear"
) {
    runs {
        val source = requirePlayer()
        doClear(source)
    }
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doClear(target)
        }
    }
}

private fun CommandContext<CommandSender>.doClear(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.clear.other")

    for (i in 0..<(target.inventory.size + 4)) {
        target.inventory.clear(i)
    }

    val entityPlayer = (target.base as CraftPlayer).handle
    entityPlayer.inventory.b(null as? ItemStack)
    entityPlayer.z()

    val craftInventory = (entityPlayer.defaultContainer as ContainerPlayer).craftInventory
    for (i in 0..<craftInventory.size) {
        craftInventory.setItem(i, null)
    }

    if (self)
        source.sendMessage(tl("command.clear"))
    else
        source.sendMessage(tl("command.clear.other", target.name))
}