package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
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
    if (target == null || !target.isOnline)
        throw TranslatableException("command.r.none")

    source.replyingTo = target
    source.sendMessage(local("command.msg.to", target.displayName, message))
    Bukkit.getOnlinePlayers()
        .filter { it.uniqueId != source.uniqueId && it.uniqueId != target.uniqueId }
        .filter { it.core().data.isSocialSpy }
        .forEach {
            it.sendMessage(local("command.socialspy.msg",
                source.displayName, target.displayName, message))
        }

    if (target.data.ignores(source.data) && !source.isOp && !source.hasPermission("zcore.ignore.bypass"))
        return

    target.replyingTo = source
    target.sendMessage(local("command.msg.from", source.displayName, message))
}