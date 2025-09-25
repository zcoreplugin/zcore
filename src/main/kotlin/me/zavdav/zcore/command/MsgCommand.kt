package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.notifySocialSpy
import org.bukkit.command.CommandSender

internal val msgCommand = command(
    "msg",
    arrayOf("m", "tell", "t", "whisper", "w"),
    "Sends a private message to a player",
    "zcore.msg"
) {
    playerArgument("player") {
        textArgument("message") {
            runs {
                val player: CorePlayer by this
                val message: String by this
                doMsg(player, message)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMsg(target: CorePlayer, message: String) {
    val source = requirePlayer()
    var finalMessage = message
    if (source.hasPermission("zcore.msg.color"))
        finalMessage = message.colored()

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