package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.notifySocialSpy
import org.bukkit.command.CommandSender

internal val rCommand = command(
    "r",
    arrayOf("reply"),
    "Replies to the last private message",
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
    if (target == null || !target.isOnline)
        throw TranslatableException("command.r.none")

    source.replyingTo = target
    source.sendMessage(local("command.msg.to", target.displayName, message))
    notifySocialSpy(
        local("command.socialspy.msg", source.displayName, target.displayName, message),
        source.uniqueId, target.uniqueId
    )

    if (target.data.checkIgnoring(source)) return
    target.replyingTo = source
    target.sendMessage(local("command.msg.from", source.displayName, message))
}