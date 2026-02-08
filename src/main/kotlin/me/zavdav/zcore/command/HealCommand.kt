package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val healCommand = command(
    "heal",
    "Heals a player",
    "zcore.heal"
) {
    runs {
        val source = requirePlayer()
        doHeal(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.heal.other")
        runs {
            val player: Player by this
            doHeal(player)
        }
    }
}

private fun CommandContext<CommandSender>.doHeal(target: Player) {
    target.health = 20
    source.sendMessage(local("command.heal", target.name))
}