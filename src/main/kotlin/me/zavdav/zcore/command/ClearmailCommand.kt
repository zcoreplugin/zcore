package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val clearmailCommand = command(
    "clearmail",
    "Clears a player's mail",
    "zcore.clearmail"
) {
    runs {
        val source = requirePlayer()
        doClearmail(source.data)
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.clearmail.other")
        runs {
            val player: OfflinePlayer by this
            doClearmail(player)
        }
    }
}

private fun CommandContext<CommandSender>.doClearmail(target: OfflinePlayer) {
    if (target.clearMail()) {
        source.sendMessage(local("command.clearmail", target.name))
    } else {
        throw TranslatableException("command.clearmail.none", target.name)
    }
}