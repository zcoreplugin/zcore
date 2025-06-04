package me.zavdav.zcore.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World

private val AIR_MATERIALS = setOf(
    Material.AIR.id,
    Material.SAPLING.id,
    Material.POWERED_RAIL.id,
    Material.DETECTOR_RAIL.id,
    Material.LONG_GRASS.id,
    Material.DEAD_BUSH.id,
    Material.YELLOW_FLOWER.id,
    Material.RED_ROSE.id,
    Material.BROWN_MUSHROOM.id,
    Material.RED_MUSHROOM.id,
    Material.TORCH.id,
    Material.FIRE.id,
    Material.REDSTONE_WIRE.id,
    Material.CROPS.id,
    Material.SIGN_POST.id,
    Material.WOODEN_DOOR.id,
    Material.LADDER.id,
    Material.RAILS.id,
    Material.WALL_SIGN.id,
    Material.LEVER.id,
    Material.STONE_PLATE.id,
    Material.IRON_DOOR_BLOCK.id,
    Material.WOOD_PLATE.id,
    Material.REDSTONE_TORCH_OFF.id,
    Material.REDSTONE_TORCH_ON.id,
    Material.STONE_BUTTON.id,
    Material.SNOW.id,
    Material.SUGAR_CANE_BLOCK.id,
    Material.PORTAL.id,
    Material.DIODE_BLOCK_OFF.id,
    Material.DIODE_BLOCK_ON.id,
    Material.TRAP_DOOR.id
)

private val DANGEROUS_MATERIALS = setOf(
    Material.LAVA.id,
    Material.STATIONARY_LAVA.id,
    Material.FIRE.id,
    Material.CACTUS.id
)

private fun isBlockAboveAir(world: World, x: Int, y: Int, z: Int): Boolean =
    if (y >= 129) true else world.getBlockAt(x, y - 1, z).typeId in AIR_MATERIALS

private fun isBlockSafe(world: World, x: Int, y: Int, z: Int): Boolean {
    val type = if (y >= 128) 0 else world.getBlockAt(x, y, z).typeId
    val typeAbove = if (y + 1 >= 128) 0 else world.getBlockAt(x, y + 1, z).typeId
    val typeBelow = if (y - 1 >= 128) 0 else world.getBlockAt(x, y - 1, z).typeId

    return type in AIR_MATERIALS && type !in DANGEROUS_MATERIALS &&
           typeAbove in AIR_MATERIALS && typeAbove !in DANGEROUS_MATERIALS &&
           typeBelow !in AIR_MATERIALS && typeBelow !in DANGEROUS_MATERIALS
}

internal fun Location.getSafe(): Location? {
    val location = clone()
    val world = location.world
    var x = location.blockX
    var y = location.blockY + 1
    val z = location.blockZ
    val initialY = y

    repeat(32) {
        while (isBlockAboveAir(world, x, y, z) && y > 1)
            y--
        while (y < 128) {
            if (isBlockSafe(world, x, y, z))
                return Location(world, x + 0.5, y.toDouble(), z + 0.5, location.yaw, location.pitch)
            y++
        }
        y = initialY
        x++
    }

    return null
}

internal fun Location.normalizedDirection(): Location {
    val location = clone()
    location.pitch = 0f
    val yaw = (location.yaw % 360 + 360) % 360
    when {
        yaw >= 45 && yaw < 135 -> location.yaw = 90f
        yaw >= 135 && yaw < 225 -> location.yaw = 180f
        yaw >= 225 && yaw < 315 -> location.yaw = 270f
        else -> location.yaw = 0f
    }
    return location
}