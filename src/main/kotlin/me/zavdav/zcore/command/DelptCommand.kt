package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.local
import org.bukkit.Material
import org.bukkit.command.CommandSender

internal val delptCommand = command(
    "delpt",
    "Deletes the power tool attached to the item in your hand.",
    "/delpt [<player>]",
    "zcore.delpt"
) {
    runs {
        val source = requirePlayer()
        doDelpt(source.data)
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doDelpt(target)
        }
    }
}

private fun CommandContext<CommandSender>.doDelpt(target: OfflinePlayer) {
    val source = requirePlayer()
    val self = source.uniqueId == target.uuid
    if (!self) require("zcore.delpt.other")

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