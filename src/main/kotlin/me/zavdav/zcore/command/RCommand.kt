package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.MessageSendEvent
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.colored
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

    var finalMessage = message
    if (source.hasPermission("zcore.msg.color"))
        finalMessage = message.colored()

    if (!MessageSendEvent(source, target, finalMessage).call()) return
    source.replyingTo = target
    source.sendMessage(local("command.msg.to", target.displayName, finalMessage))
    notifySocialSpy(
        local("command.socialspy.msg", source.displayName, target.displayName, finalMessage),
        source.uniqueId, target.uniqueId
    )

    if (target.data.checkIgnoring(source)) return
    target.replyingTo = source
    target.sendMessage(local("command.msg.from", source.displayName, finalMessage))
}