package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val weatherCommand = command(
    "weather",
    "Changes the weather.",
    "/weather (clear|rain|thunder)",
    "zcore.weather"
) {
    literal("clear") {
        runs {
            doWeatherClear()
        }
    }
    literal("rain") {
        runs {
            doWeatherRain()
        }
    }
    literal("thunder") {
        runs {
            doWeatherThunder()
        }
    }
}

private fun CommandContext<CommandSender>.doWeatherClear() {
    val world = Bukkit.getWorlds()[0]
    if (world.hasStorm()) {
        world.isThundering = false
        world.weatherDuration = 1
    }
    source.sendMessage(tl("command.weather.clear"))
}

private fun CommandContext<CommandSender>.doWeatherRain() {
    val world = Bukkit.getWorlds()[0]
    if (!world.hasStorm()) {
        world.weatherDuration = 1
    }
    world.isThundering = false
    source.sendMessage(tl("command.weather.rain"))
}

private fun CommandContext<CommandSender>.doWeatherThunder() {
    val world = Bukkit.getWorlds()[0]
    if (!world.hasStorm()) {
        world.weatherDuration = 1
    }
    world.isThundering = true
    source.sendMessage(tl("command.weather.thunder"))
}