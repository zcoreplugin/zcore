package me.zavdav.zcore.data.location

import me.zavdav.zcore.data.WorldSpawns
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents the spawn point of a world. */
class WorldSpawn(id: EntityID<UUID>) : Location(id) {
    companion object : UUIDEntityClass<WorldSpawn>(WorldSpawns)
}