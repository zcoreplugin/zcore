package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.TeleportRequest
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncDelayedTask
import org.bukkit.command.CommandSender

internal val tpaCommand = command(
    "tpa",
    "Sends a request to teleport to a player.",
    "/tpa <player>",
    "zcore.tpa"
) {
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doTpa(target)
        }
    }
}

private fun CommandContext<CommandSender>.doTpa(target: CorePlayer) {
    val source = requirePlayer()
    if (target.teleportRequests.any { it.source == source })
        throw TranslatableException("command.tpa.alreadySent", target.name)

    source.sendMessage(local("command.tpa", target.name))
    if (target.data.ignores(source.data) && !source.isOp && !source.hasPermission("zcore.ignore.bypass"))
        return

    val request = TeleportRequest(source, false)
    val expiresAfter = ZCoreConfig.getInt("command.tpa.expire-after")
    target.teleportRequests.add(request)

    syncDelayedTask(expiresAfter * 20L) {
        if (target.teleportRequests.remove(request)) {
            source.sendMessage(local("command.tpa.expired", target.name))
            target.sendMessage(local("command.tpa.incoming.expired", source.name))
        }
    }

    target.sendMessage(local("command.tpa.incoming", source.name))
    target.sendMessage(local("command.tpa.incoming.use"))
    target.sendMessage(local("command.tpa.incoming.expiration", expiresAfter))
}