package me.zavdav.zcore.player

import me.zavdav.zcore.data.PowerTools
import org.bukkit.Material
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a power tool that belongs to a player. */
class PowerTool internal constructor(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<PowerTool>(PowerTools)

    /** The player this power tool belongs to. */
    var player by OfflinePlayer referencedOn PowerTools.player
        internal set

    /** The item material. */
    var material: Material by PowerTools.material
        internal set

    /** The item data. */
    var data: Short by PowerTools.data
        internal set

    /** The command this power tool executes. */
    var command: String by PowerTools.command
        internal set
}