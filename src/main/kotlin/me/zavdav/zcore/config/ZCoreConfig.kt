package me.zavdav.zcore.config

import me.zavdav.zcore.ZCore
import org.bukkit.util.config.Configuration
import java.io.File

object ZCoreConfig {

    private val yaml = Configuration(File(ZCore.INSTANCE.dataFolder, "config.yml"))
    private val map = mutableMapOf<String, Any>()

    fun load() {
        yaml.load()

        put("command.afk.auto.enabled", true)
        put("command.afk.auto.time", 300, 1)
        put("command.afk.auto.kick.enabled", true)
        put("command.afk.auto.kick.time", 1800, 1)
        put("command.afk.protect", false)
        put("command.ban.default-reason", "The ban hammer has spoken!")
        put("command.banip.default-reason", "The ban hammer has spoken!")
        put("command.give.default-amount", 1, 1)
        put("command.kick.default-reason", "Kicked from server")
        put("command.list.group-order", listOf("admin", "moderator", "donator", "default"))
        put("command.mute.default-reason", "A mysterious force leaves you speechless!")
        put("command.rules.lines", listOf("&7--------------- &fServer Rules &7---------------", "1. Placeholder text", "2. Placeholder text", "3. Placeholder text"))
        put("command.tp.max-radius", 100000, 1)
        put("command.tpa.expire-after", 30, 1)
        put("command.tpahere.expire-after", 30, 1)
        put("command.disabled", ArrayList())
        put("command.overridden", ArrayList())
        put("text.command-prefix", "&7>>")
        put("text.currency", "$")
        put("text.chat-format", "{PLAYER}: {MESSAGE}")
        put("text.nick-prefix", "~")
        put("text.display-name-format", "{PREFIX} {NICKNAME} {SUFFIX}")
        put("text.join-message", "&e{PLAYER} joined the game.")
        put("text.quit-message", "&e{PLAYER} left the game.")
        put("text.kick-message", "&e{PLAYER} was kicked from the game.")
        put("text.ban-message", "&e{PLAYER} was banned from the server.")
        put("text.new-player-announcement", "&eWelcome to the server, {PLAYER}!")
        put("text.message-of-the-day", listOf("&6Welcome, {NAME}!", "&7-------------------------", "&eOnline players: &b{PLAYERCOUNT}/{MAXPLAYERS}"))

        yaml.save()
    }

    private fun put(key: String, default: String) {
        var value = yaml.getProperty(key) as? String
        if (value == null) {
            value = default
            yaml.setProperty(key, value)
        }
        map[key] = value
    }

    private fun put(key: String, default: Int, min: Int) {
        var value = (yaml.getProperty(key) as? Number)?.toInt()?.coerceAtLeast(min)
        if (value == null) {
            value = default.coerceAtLeast(min)
            yaml.setProperty(key, value)
        }
        map[key] = value
    }

    private fun put(key: String, default: Boolean) {
        var value = yaml.getProperty(key) as? Boolean
        if (value == null) {
            value = default
            yaml.setProperty(key, value)
        }
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    private fun put(key: String, default: List<String>) {
        var value = yaml.getProperty(key) as? List<String>
        if (value == null) {
            value = default
            yaml.setProperty(key, value)
        }
        map[key] = value
    }

    fun getString(key: String): String = map[key] as String

    fun getInt(key: String): Int = map[key] as Int

    fun getBoolean(key: String): Boolean = map[key] as Boolean

    @Suppress("UNCHECKED_CAST")
    fun getStringList(key: String): List<String> = map[key] as List<String>

}