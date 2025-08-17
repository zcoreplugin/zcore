package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val delhomeCommand = command(
    "delhome",
    arrayOf("dh"),
    "Deletes a player's home",
    "zcore.delhome"
) {
    stringArgument("home") {
        runs {
            val source = requirePlayer()
            val home: String by this
            doDelhome(source.data, home)
        }
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.delhome.other")
        stringArgument("home") {
            runs {
                val player: OfflinePlayer by this
                val home: String by this
                doDelhome(player, home)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doDelhome(target: OfflinePlayer, homeName: String) {
    val existingHome = target.deleteHome(homeName)
    if (existingHome != null) {
        source.sendMessage(local("command.delhome", target.name, existingHome.name))
    } else {
        throw TranslatableException("command.delhome.unknown", target.name, homeName)
    }
}