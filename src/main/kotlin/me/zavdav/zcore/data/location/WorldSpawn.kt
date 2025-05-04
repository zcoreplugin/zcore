package me.zavdav.zcore.data.location

import me.zavdav.zcore.data.WorldSpawns
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents the spawn point of a world. */
class WorldSpawn(id: EntityID<UUID>) : Location(id) {

    internal companion object : UUIDEntityClass<WorldSpawn>(WorldSpawns) {
        fun new(
            world: String,
            x: Double,
            y: Double,
            z: Double,
            pitch: Float,
            yaw: Float
        ): WorldSpawn {
            val base = Location.new(world, x, y, z, pitch, yaw)
            return new(base.id.value) {}
        }
    }

}