package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val ignoreCommand = command(
    "ignore",
    "Ignores/unignores a player",
    "zcore.ignore"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doIgnore(player)
        }
    }
}

private fun CommandContext<CommandSender>.doIgnore(target: OfflinePlayer) {
    val source = requirePlayer()
    if (source.uniqueId == target.uuid)
        throw TranslatableException("command.ignore.self")

    if (!source.data.ignores(target)) {
        source.data.addIgnore(target)
        source.sendMessage(local("command.ignore.add", target.name))
    } else {
        source.data.removeIgnore(target)
        source.sendMessage(local("command.ignore.remove", target.name))
    }
}