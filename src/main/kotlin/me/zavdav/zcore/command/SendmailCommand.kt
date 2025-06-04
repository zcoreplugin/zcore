package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val sendmailCommand = command(
    "sendmail",
    "Sends mail to a player.",
    "/sendmail <player> <message>",
    "zcore.sendmail"
) {
    offlinePlayerArgument("target") {
        stringArgument("message", StringType.GREEDY_STRING) {
            runs(permission) {
                val target: OfflinePlayer by this
                val message: String by this
                doSendmail(target, message)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doSendmail(target: OfflinePlayer, message: String) {
    val source = requirePlayer()
    source.data.sendMail(target, message)
    source.sendMessage(tl("command.sendmail.success", target.name))
}