package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val broadcastCommand = command(
    "broadcast",
    arrayOf("bc"),
    "Broadcasts a message to all players.",
    "/broadcast <message>",
    "zcore.broadcast"
) {
    textArgument("message") {
        runs {
            val message: String by this
            doBroadcast(message)
        }
    }
}

private fun CommandContext<CommandSender>.doBroadcast(message: String) {
    val source = this.source
    var broadcast = message
    if (source.isOp || source.hasPermission("zcore.broadcast.color"))
        broadcast = broadcast.colored()

    Bukkit.broadcastMessage(tl("command.broadcast", broadcast))
}