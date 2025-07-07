package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val delhomeCommand = command(
    "delhome",
    arrayOf("dh"),
    "Deletes a home.",
    "/delhome <name>",
    "zcore.delhome"
) {
    stringArgument("homeName") {
        runs {
            val source = requirePlayer()
            val homeName: String by this
            doDelhome(source.data, homeName)
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("homeName") {
            runs {
                val target: OfflinePlayer by this
                val homeName: String by this
                doDelhome(target, homeName)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doDelhome(target: OfflinePlayer, homeName: String) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.delhome.other")

    val existingHome = target.deleteHome(homeName)
    if (existingHome != null) {
        source.sendMessage(local("command.delhome", target.name, existingHome.name))
    } else {
        throw TranslatableException("command.delhome.unknown", target.name, homeName)
    }
}