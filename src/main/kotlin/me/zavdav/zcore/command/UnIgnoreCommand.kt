package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.PlayerUnignoreEvent
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val unignoreCommand = command(
    "unignore",
    "Makes a player stop ignoring a player",
    "zcore.unignore"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doUnIgnore(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnIgnore(target: OfflinePlayer) {
    val source = requirePlayer()
    if (!PlayerUnignoreEvent(source, target).call()) return
    source.data.removeIgnore(target)
    source.sendMessage(local("command.unignore", target.name))
}