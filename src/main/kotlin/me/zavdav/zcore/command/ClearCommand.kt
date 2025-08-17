package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import net.minecraft.server.ContainerPlayer
import net.minecraft.server.ItemStack
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.entity.CraftPlayer

internal val clearCommand = command(
    "clear",
    "Clears a player's inventory",
    "zcore.clear"
) {
    runs {
        val source = requirePlayer()
        doClear(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.clear.other")
        runs {
            val player: CorePlayer by this
            doClear(player)
        }
    }
}

private fun CommandContext<CommandSender>.doClear(target: CorePlayer) {
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

    source.sendMessage(local("command.clear", target.name))
}