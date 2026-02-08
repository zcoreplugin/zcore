package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.TeleportAcceptEvent
import me.zavdav.zcore.player.teleportRequests
import me.zavdav.zcore.player.teleportTo
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val tpacceptCommand = command(
    "tpaccept",
    arrayOf("tpyes"),
    "Accepts a teleport request",
    "zcore.tpaccept"
) {
    runs {
        doTpAccept()
    }
    playerArgument("player") {
        runs {
            val player: Player by this
            doTpAccept(player)
        }
    }
}

private fun CommandContext<CommandSender>.doTpAccept() {
    val source = requirePlayer()
    val request = source.teleportRequests.peek()
    if (request == null || request.ignore)
        throw TranslatableException("command.tpa.none")

    val requester = request.source
    if (!TeleportAcceptEvent(source, request).call()) return
    source.teleportRequests.poll()
    source.sendMessage(local("command.tpaccept", requester.name))
    requester.sendMessage(local("command.tpaccept.notify", source.name))

    if (request.here) {
        source.teleportTo(requester)
    } else {
        requester.teleportTo(source)
    }
}

private fun CommandContext<CommandSender>.doTpAccept(requester: Player) {
    val source = requirePlayer()
    val request = source.teleportRequests.firstOrNull { it.source == requester }
    if (request == null || request.ignore)
        throw TranslatableException("command.tpa.none.player", requester.name)

    if (!TeleportAcceptEvent(source, request).call()) return
    source.teleportRequests.remove(request)
    source.sendMessage(local("command.tpaccept", requester.name))
    requester.sendMessage(local("command.tpaccept.notify", source.name))

    if (request.here) {
        source.teleportTo(requester)
    } else {
        requester.teleportTo(source)
    }
}