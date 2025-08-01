package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.MaterialData
import me.zavdav.zcore.util.local
import org.bukkit.Material
import org.bukkit.command.CommandSender

internal val setptCommand = command(
    "setpt",
    "Sets the item in your hand as a power tool.",
    "/setpt <command>",
    "zcore.setpt"
) {
    textArgument("command") {
        runs {
            val command: String by this
            doSetpt(command)
        }
    }
}

private fun CommandContext<CommandSender>.doSetpt(command: String) {
    val source = requirePlayer()
    val item = source.itemInHand
    if (item.type == Material.AIR)
        throw TranslatableException("command.setpt.noItem")

    source.data.setPowerTool(item.type, item.durability, command)
    val materialData = MaterialData(item.type, item.durability)
    source.sendMessage(local("command.setpt", materialData.displayName, source.name))
}