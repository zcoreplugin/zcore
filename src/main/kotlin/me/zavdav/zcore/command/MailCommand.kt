package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val mailCommand = command(
    "mail",
    "Manages a player's mail",
    "zcore.mail"
) {
    literal("send") {
        offlinePlayerArgument("player") {
            textArgument("message") {
                runs {
                    val player: OfflinePlayer by this
                    val message: String by this
                    doMailSend(player, message)
                }
            }
        }
    }
    literal("read") {
        runs {
            val source = requirePlayer()
            doMailRead(source.data)
        }
        offlinePlayerArgument("player") {
            requiresPermission("zcore.mail.read.other")
            runs {
                val player: OfflinePlayer by this
                doMailRead(player)
            }
        }
    }
    literal("clear") {
        runs {
            doMailClear()
        }
    }
}

private fun CommandContext<CommandSender>.doMailSend(target: OfflinePlayer, message: String) {
    val source = requirePlayer()
    var finalMessage = message
    if (source.hasPermission("zcore.mail.send.color"))
        finalMessage = message.colored()
    source.sendMessage(local("command.mail.send", target.name))
    Bukkit.getOnlinePlayers()
        .filter { it.uniqueId != source.uniqueId && it.uniqueId != target.uuid }
        .filter { it.core().data.isSocialSpy }
        .forEach {
            it.sendMessage(local("command.socialspy.mail",
                source.displayName, target.name, finalMessage))
        }

    if (target.ignores(source.data) && !source.hasPermission("zcore.ignore.bypass"))
        return

    source.data.sendMail(target, finalMessage)
}

private fun CommandContext<CommandSender>.doMailRead(target: OfflinePlayer) {
    val mail = target.mail.reversed()
    if (mail.isEmpty())
        throw TranslatableException("command.mail.read.none", target.name)

    source.sendMessage(local("command.mail.read", target.name))
    source.sendMessage(line(ChatColor.GRAY))
    mail.forEach {
        source.sendMessage(local("command.mail.read.line", it.sender.name, it.message))
    }
}

private fun CommandContext<CommandSender>.doMailClear() {
    val source = requirePlayer()
    source.data.clearMail()
    source.sendMessage(local("command.mail.clear", source.name))
}