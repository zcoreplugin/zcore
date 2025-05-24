package me.zavdav.zcore.config

import me.zavdav.zcore.ZCore
import org.bukkit.util.config.Configuration
import java.io.File
import java.nio.file.Files

object Config {

    private val file = File(ZCore.INSTANCE.dataFolder, "config.yml")
    private lateinit var yaml: Configuration

    internal fun load() {
        val stream = this::class.java.getResourceAsStream("/config.yml")!!
        if (!file.exists()) {
            file.parentFile.mkdirs()
            Files.copy(stream, file.toPath())
        }

        yaml = Configuration(file)
        yaml.load()
    }

    private fun getString(key: String, def: String): String =
        yaml.getProperty(key) as? String ?: def

    private fun getInt(key: String, min: Int = 0, def: Int): Int =
        (yaml.getProperty(key) as? Number)?.toInt()?.coerceAtLeast(min) ?: def

    private fun getBoolean(key: String, def: Boolean): Boolean =
        yaml.getProperty(key) as? Boolean ?: def

    private fun getStringList(key: String, def: List<String>): List<String> =
        yaml.getProperty(key) as? List<String> ?: def

    val configVersion: Int get() = getInt("config-version", def = 0)

    val prefix: String get() = getString("prefix", "&5[&dZCore&5] &7>> ")

    val motd: List<String> get() = getStringList("motd", emptyList())
    val rules: List<String> get() = getStringList("rules", emptyList())

    val disabledCommands: List<String> get() = getStringList("commands.disabled", emptyList())
    val overrideCommands: List<String> get() = getStringList("commands.override", emptyList())
    val showOtherCommands: Boolean get() = getBoolean("commands.show-other-in-help", true)

    val firstJoinFormat: String get() = getString("format.first-join", "&eWelcome to the server, {NAME}!")
    val joinFormat: String get() = getString("format.join", "&e{NAME} joined the game.")
    val leaveFormat: String get() = getString("format.leave", "&e{NAME} left the game.")
    val kickFormat: String get() = getString("format.kick", "&e{NAME} was kicked from the game.")
    val banFormat: String get() = getString("format.ban", "&e{NAME} was banned from the server.")

    val displayNameFormat: String get() = getString("format.display-name", "{PREFIX} &f{NICKNAME}&f {SUFFIX}")
    val operatorColor: String get() = getString("format.operator-color", "none")
    val nickPrefix: String get() = getString("format.nick-prefix", "~")
    val currency: String get() = getString("format.currency", "$")

    val chatFormat: String get() = getString("format.chat", "{DISPLAYNAME}&f: {MESSAGE}")
    val broadcastFormat: String get() = getString("format.broadcast", "&d[Broadcast] {MESSAGE}")
    val sendMsgFormat: String get() = getString("format.send-msg", "&7[me -> &f{DISPLAYNAME}&7] &f{MESSAGE}")
    val receiveMsgFormat: String get() = getString("format.receive-msg", "&7[&f{DISPLAYNAME}&7 -> me] &f{MESSAGE}")
    val mailFormat: String get() = getString("format.mail", "{DISPLAYNAME}&f: {MESSAGE}")
    val socialspyFormat: String get() = getString("format.socialspy", "&5[SocialSpy] &f{DISPLAYNAME}&f: {COMMAND}")

    val afkEnabled: Boolean get() = getBoolean("afk.enabled", true)
    val afkTime: Int get() = getInt("afk.time", 1, 300)
    val afkKickEnabled: Boolean get() = getBoolean("afk.kick.enabled", true)
    val afkKickTime: Int get() = getInt("afk.kick.time", 1, 1800)
    val afkProtection: Boolean get() = getBoolean("afk.protect", false)

    val teleportDelay: Int get() = getInt("teleport-delay", def = 3)
    val chatRadius: Int get() = getInt("chat-radius", def = 0)
    val defaultGiveAmount: Int get() = getInt("default-give-amount", 1, 64)

}