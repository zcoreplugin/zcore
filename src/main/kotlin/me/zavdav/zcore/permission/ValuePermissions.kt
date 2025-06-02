package me.zavdav.zcore.permission

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import org.bukkit.util.config.Configuration
import java.io.File

/** Used to get and set permissions with integer values. */
object ValuePermissions {

    private val file = File(ZCore.INSTANCE.dataFolder, "value-permissions.yml")
    private val yaml = Configuration(file)

    internal fun load() {
        yaml.load()
    }

    internal fun save() {
        yaml.save()
    }

    /** Gets the value of a [permission] for a [player], or [default] if it is not set. */
    fun getPermissionValue(player: OfflinePlayer, permission: String, default: Int): Int {
        val map = yaml.getProperty(player.uuid.toString())
        if (map == null || map !is Map<*, *>) {
            yaml.setProperty(player.uuid.toString(), mutableMapOf<String, Any>())
            return default
        }
        return map[permission]?.toString()?.toIntOrNull() ?: default
    }

    /** Sets the [value] of a [permission] for a [player]. */
    fun setPermissionValue(player: OfflinePlayer, permission: String, value: Int) {
        var map = yaml.getProperty(player.uuid.toString()) as? MutableMap<String, Any>
        if (map == null) {
            val newMap = mutableMapOf<String, Any>()
            yaml.setProperty(player.uuid.toString(), newMap)
            map = newMap
        }
        map[permission] = value
    }

    /** Adds [delta] to the value of a [permission] for a [player]. */
    fun addToPermissionValue(player: OfflinePlayer, permission: String, delta: Int) {
        val currentVal = getPermissionValue(player, permission, 0)
        setPermissionValue(player, permission, currentVal + delta)
    }

}