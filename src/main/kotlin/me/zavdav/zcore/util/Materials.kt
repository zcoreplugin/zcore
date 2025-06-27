package me.zavdav.zcore.util

import org.bukkit.Material
import java.io.BufferedReader
import java.io.InputStreamReader

internal object Materials {

    private val materials = mutableMapOf<String, MaterialData>()

    internal fun load() {
        val stream = this::class.java.getResourceAsStream("/materials.csv")!!

        BufferedReader(InputStreamReader(stream)).forEachLine {
            if (it.isEmpty()) return@forEachLine

            val components = it.split(",", limit = 3)
            val type = Material.getMaterial(components[1].toInt().coerceAtLeast(1)) ?: return@forEachLine
            val data = components.getOrNull(2)?.toShort()?.coerceAtLeast(0) ?: 0
            materials[components[0].lowercase()] = MaterialData(type, data)
        }
    }

    internal fun getByName(name: String): MaterialData? = materials[name.lowercase()]

}