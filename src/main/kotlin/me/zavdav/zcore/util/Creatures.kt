package me.zavdav.zcore.util

import org.bukkit.entity.CreatureType

internal val CreatureType.displayName: String
    get() = runCatching { local("creature.${name.lowercase().replace("_", "")}") }
        .getOrElse { local("creature.unknown") }