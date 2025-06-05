package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import org.bukkit.command.CommandSender

internal val rCommand = command(
    "r",
    arrayOf("reply"),
    "A shorthand for /msg to reply to the last player.",
    "/r <message>",
    "zcore.r"
) {
    textArgument("message") {
        runs {
            val message: String by this
            doR(message)
        }
    }
}

private fun CommandContext<CommandSender>.doR(message: String) {
    val source = requirePlayer()
    val replyingTo = source.replyingTo
    if (replyingTo == null || !replyingTo.isOnline) {
        throw TranslatableException("command.r.noOne")
    }

    source.privateMessage(replyingTo, message)
}