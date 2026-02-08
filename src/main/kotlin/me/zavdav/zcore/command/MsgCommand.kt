package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.MessageSendEvent
import me.zavdav.zcore.player.data
import me.zavdav.zcore.player.replyingTo
import me.zavdav.zcore.player.zcoreDisplayName
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.checkMuted
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.notifySocialSpy
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val msgCommand = command(
    "msg",
    arrayOf("m", "tell", "t", "whisper", "w"),
    "Sends a private message to a player",
    "zcore.msg"
) {
    playerArgument("player") {
        textArgument("message") {
            runs {
                val player: Player by this
                val message: String by this
                doMsg(player, message)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMsg(target: Player, message: String) {
    val source = requirePlayer()
    var finalMessage = message
    if (source.hasPermission("zcore.msg.color"))
        finalMessage = message.colored()

    if (source.checkMuted()) return
    if (!MessageSendEvent(source, target, finalMessage).call()) return
    source.replyingTo = target
    source.sendMessage(local("command.msg.to", target.zcoreDisplayName, finalMessage))
    notifySocialSpy(
        local("command.socialspy.msg", source.zcoreDisplayName, target.zcoreDisplayName, finalMessage),
        source.uniqueId, target.uniqueId
    )

    if (target.data.checkIgnoring(source)) return
    target.replyingTo = source
    target.sendMessage(local("command.msg.from", source.zcoreDisplayName, finalMessage))
}