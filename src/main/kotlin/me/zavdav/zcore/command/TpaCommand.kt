package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.TeleportRequestEvent
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.TeleportRequest
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncDelayedTask
import org.bukkit.command.CommandSender

internal val tpaCommand = command(
    "tpa",
    "Sends a request to teleport to a player",
    "zcore.tpa"
) {
    playerArgument("player") {
        runs {
            val player: CorePlayer by this
            doTpa(player)
        }
    }
}

private fun CommandContext<CommandSender>.doTpa(target: CorePlayer) {
    val source = requirePlayer()
    if (target.teleportRequests.any { it.source == source })
        throw TranslatableException("command.tpa.alreadySent", target.name)

    val expiresAfter = ZCoreConfig.getInt("command.tpa.expire-after")
    val ignoring = target.data.checkIgnoring(source)
    val request = TeleportRequest(source, false, ignoring)

    if (!TeleportRequestEvent(source, target, request).call()) return
    target.teleportRequests.add(request)
    source.sendMessage(local("command.tpa", target.name))

    syncDelayedTask(expiresAfter * 20L) {
        if (target.teleportRequests.remove(request)) {
            source.sendMessage(local("command.tpa.expired", target.name))
            if (!ignoring) target.sendMessage(local("command.tpa.incoming.expired", source.name))
        }
    }

    if (!ignoring) {
        target.sendMessage(local("command.tpa.incoming", source.name))
        target.sendMessage(local("command.tpa.incoming.use"))
        target.sendMessage(local("command.tpa.incoming.expiration", expiresAfter))
    }
}