package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.util.colored
import org.bukkit.command.CommandSender

internal val rulesCommand = command(
    "rules",
    "Shows the server rules.",
    "/rules",
    "zcore.rules"
) {
    runs {
        doRules()
    }
}

private fun CommandContext<CommandSender>.doRules() {
    ZCoreConfig.getStringList("command.rules.lines").forEach { source.sendMessage(it.colored()) }
}