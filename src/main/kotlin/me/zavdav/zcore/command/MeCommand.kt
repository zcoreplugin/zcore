package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.data
import me.zavdav.zcore.player.zcoreDisplayName
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.checkMuted
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val meCommand = command(
    "me",
    "Broadcasts a message in the context of a player",
    "zcore.me"
) {
    textArgument("message") {
        runs {
            val message: String by this
            doMe(message)
        }
    }
}

private fun CommandContext<CommandSender>.doMe(message: String) {
    val source = requirePlayer()
    var finalMessage = message
    if (source.hasPermission("zcore.me.color"))
        finalMessage = message.colored()

    if (source.checkMuted()) return
    Bukkit.getOnlinePlayers()
        .filter { !it.data.checkIgnoring(source) }
        .forEach { it.sendMessage(local("command.me", source.zcoreDisplayName, finalMessage)) }
}