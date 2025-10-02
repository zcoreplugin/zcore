package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.PlayerIgnoreEvent
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val ignoreCommand = command(
    "ignore",
    "Makes a player ignore a player",
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
    if (!PlayerIgnoreEvent(source, target).call()) return
    source.data.addIgnore(target)
    source.sendMessage(local("command.ignore", target.name))
}