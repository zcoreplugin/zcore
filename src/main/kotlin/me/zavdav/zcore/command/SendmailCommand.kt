package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val sendmailCommand = command(
    "sendmail",
    "Sends mail to a player.",
    "/sendmail <player> <message>",
    "zcore.sendmail"
) {
    offlinePlayerArgument("target") {
        textArgument("message") {
            runs {
                val target: OfflinePlayer by this
                val message: String by this
                doSendmail(target, message)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doSendmail(target: OfflinePlayer, message: String) {
    val source = requirePlayer()
    source.sendMessage(local("command.sendmail", target.name))
    if (target.ignores(source.data) && !source.isOp && !source.hasPermission("zcore.ignore.bypass"))
        return

    source.data.sendMail(target, message)
}