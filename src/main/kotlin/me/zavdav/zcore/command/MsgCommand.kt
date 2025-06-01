package me.zavdav.zcore.command

import me.zavdav.zcore.player.CorePlayer

internal val msgCommand = command(
    "msg",
    arrayOf("tell", "whisper"),
    "Sends a private message to a player.",
    "/msg <player> <message>",
    "zcore.msg"
) {
    playerArgument("target") {
        stringArgument("message", StringType.GREEDY_STRING) {
            runs(permission) {
                val source = requirePlayer()
                val target: CorePlayer by this
                val message: String by this
                source.privateMessage(target, message)
            }
        }
    }
}