package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val ignoreCommand = command(
    "ignore",
    "Toggles whether you ignore a player.",
    "/ignore <player>",
    "zcore.ignore"
) {
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doIgnore(target)
        }
    }
}

private fun CommandContext<CommandSender>.doIgnore(target: OfflinePlayer) {
    val source = requirePlayer()
    if (source.uniqueId == target.uuid)
        throw TranslatableException("command.ignore.self")

    if (!source.data.ignores(target)) {
        source.data.addIgnore(target)
        source.sendMessage(tl("command.ignore.add", target.name))
    } else {
        source.data.removeIgnore(target)
        source.sendMessage(tl("command.ignore.remove", target.name))
    }
}