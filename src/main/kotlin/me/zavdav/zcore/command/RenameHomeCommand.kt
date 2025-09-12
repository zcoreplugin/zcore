package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val renamehomeCommand = command(
    "renamehome",
    arrayOf("rh"),
    "Renames a player's home",
    "zcore.renamehome"
) {
    stringArgument("home") {
        stringArgument("newName") {
            runs {
                val source = requirePlayer()
                val home: String by this
                val newName: String by this
                doRenameHome(source.data, home, newName)
            }
        }
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.renamehome.other")
        stringArgument("home") {
            stringArgument("newName") {
                runs {
                    val player: OfflinePlayer by this
                    val home: String by this
                    val newName: String by this
                    doRenameHome(player, home, newName)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doRenameHome(
    target: OfflinePlayer, oldName: String, newName: String
) {
    if (!newName.matches(Regex("[a-zA-Z0-9_-]+")))
        throw TranslatableException("command.renamehome.illegal", newName)

    val existingHome = target.getHome(oldName)
    if (existingHome == null)
        throw TranslatableException("command.renamehome.unknown", target.name, oldName)
    val matchingHome = target.getHome(newName)
    if (matchingHome != null)
        throw TranslatableException("command.renamehome.exists", target.name, matchingHome.name)

    val prevName = existingHome.name
    existingHome.name = newName
    source.sendMessage(local("command.renamehome", target.name, prevName, newName))
}