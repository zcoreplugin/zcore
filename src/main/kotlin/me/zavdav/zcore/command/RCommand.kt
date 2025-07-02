package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.tl
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
    val target = source.replyingTo
    if (target == null || !target.isOnline) {
        throw TranslatableException("command.r.noOne")
    }

    source.replyingTo = target
    source.sendMessage(tl("command.msg.toPlayer", target.displayName, message))
    if (target.data.ignores(source.data) && !source.isOp && !source.hasPermission("zcore.ignore.bypass"))
        return

    target.replyingTo = source
    target.sendMessage(tl("command.msg.fromPlayer", source.displayName, message))
}