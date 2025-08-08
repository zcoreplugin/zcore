package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val renamehomeCommand = command(
    "renamehome",
    arrayOf("rh"),
    "Renames a home.",
    "/renamehome <home> <name>",
    "zcore.renamehome"
) {
    stringArgument("oldName") {
        stringArgument("newName") {
            runs {
                val source = requirePlayer()
                val oldName: String by this
                val newName: String by this
                doRenamehome(source.data, oldName, newName)
            }
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("oldName") {
            stringArgument("newName") {
                runs {
                    val target: OfflinePlayer by this
                    val oldName: String by this
                    val newName: String by this
                    doRenamehome(target, oldName, newName)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doRenamehome(
    target: OfflinePlayer, oldName: String, newName: String
) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.renamehome.other")

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