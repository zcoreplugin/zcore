package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.TeleportRequest
import me.zavdav.zcore.util.checkIgnoring
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncDelayedTask
import org.bukkit.command.CommandSender

internal val tpahereCommand = command(
    "tpahere",
    "Sends a request for a player to teleport to you",
    "zcore.tpahere"
) {
    playerArgument("player") {
        runs {
            val player: CorePlayer by this
            doTpaHere(player)
        }
    }
}

private fun CommandContext<CommandSender>.doTpaHere(target: CorePlayer) {
    val source = requirePlayer()
    if (target.teleportRequests.any { it.source == source })
        throw TranslatableException("command.tpa.alreadySent", target.name)

    source.sendMessage(local("command.tpa", target.name))
    val expiresAfter = ZCoreConfig.getInt("command.tpahere.expire-after")
    val ignoring = target.data.checkIgnoring(source)
    val request = TeleportRequest(source, true, ignoring)
    target.teleportRequests.add(request)

    syncDelayedTask(expiresAfter * 20L) {
        if (target.teleportRequests.remove(request)) {
            source.sendMessage(local("command.tpa.expired", target.name))
            if (!ignoring) target.sendMessage(local("command.tpa.incoming.expired", source.name))
        }
    }

    if (!ignoring) {
        target.sendMessage(local("command.tpa.here.incoming", source.name))
        target.sendMessage(local("command.tpa.incoming.use"))
        target.sendMessage(local("command.tpa.incoming.expiration", expiresAfter))
    }
}