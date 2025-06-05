package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import org.bukkit.command.CommandSender

internal val msgCommand = command(
    "msg",
    arrayOf("tell", "whisper"),
    "Sends a private message to a player.",
    "/msg <player> <message>",
    "zcore.msg"
) {
    playerArgument("target") {
        textArgument("message") {
            runs {
                val target: CorePlayer by this
                val message: String by this
                doMsg(target, message)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMsg(target: CorePlayer, message: String) {
    val source = requirePlayer()
    source.privateMessage(target, message)
}