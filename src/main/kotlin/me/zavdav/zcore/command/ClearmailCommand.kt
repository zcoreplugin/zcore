package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val clearmailCommand = command(
    "clearmail",
    "Clears all of your mail.",
    "/clearmail",
    "zcore.clearmail"
) {
    runs {
        val source = requirePlayer()
        doClearmail(source.data)
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doClearmail(target)
        }
    }
}

private fun CommandContext<CommandSender>.doClearmail(target: OfflinePlayer) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.clearmail.other")

    if (target.clearMail()) {
        source.sendMessage(local("command.clearmail", target.name))
    } else {
        throw TranslatableException("command.clearmail.none", target.name)
    }
}