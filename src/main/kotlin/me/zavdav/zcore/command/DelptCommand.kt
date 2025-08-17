package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.local
import org.bukkit.Material
import org.bukkit.command.CommandSender

internal val delptCommand = command(
    "delpt",
    "Deletes the power tool attached to the item in your hand",
    "zcore.delpt"
) {
    runs {
        val source = requirePlayer()
        doDelpt(source.data)
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.delpt.other")
        runs {
            val player: OfflinePlayer by this
            doDelpt(player)
        }
    }
}

private fun CommandContext<CommandSender>.doDelpt(target: OfflinePlayer) {
    val source = requirePlayer()
    val item = source.itemInHand
    if (item.type == Material.AIR)
        throw TranslatableException("command.delpt.noItem")

    val materialData = MaterialData(item.type, item.durability)
    if (target.deletePowerTool(item.type, item.durability) != null) {
        source.sendMessage(local("command.delpt", target.name, materialData.displayName))
    } else {
        throw TranslatableException("command.delpt.unknown", target.name, materialData.displayName)
    }
}