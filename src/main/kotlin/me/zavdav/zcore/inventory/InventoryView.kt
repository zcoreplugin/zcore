package me.zavdav.zcore.inventory

import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.getField
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncRepeatingTask
import net.minecraft.server.ContainerPlayer
import net.minecraft.server.EntityHuman
import net.minecraft.server.EntityPlayer
import net.minecraft.server.IInventory
import net.minecraft.server.Packet102WindowClick
import net.minecraft.server.Packet106Transaction
import net.minecraft.server.Slot
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftInventory
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

internal class InventoryView(val viewer: CorePlayer, target: CorePlayer) {

    private var taskId: Int = 0
    private val viewerInventory: CraftInventory = CraftInventory(NmsInventory(target.name))
    private val targetInventory: PlayerInventory = target.inventory
    private val nmsViewer: EntityPlayer = (viewer.base as CraftPlayer).handle
    private val nmsTarget: EntityPlayer = (target.base as CraftPlayer).handle

    fun open() {
        viewer.inventoryView = this
        nmsViewer.a(viewerInventory.inventory)
        taskId = syncRepeatingTask(0, 1) { update() }

        for (slot in VIEW_EMPTY) {
            viewerInventory.setItem(slot, ItemStack(Material.DIODE_BLOCK_ON, 1))
        }
    }

    fun update() {
        if (nmsViewer.activeContainer == nmsViewer.defaultContainer || !viewer.isOnline) {
            Bukkit.getScheduler().cancelTask(taskId)
            viewer.inventoryView = null
            return
        }

        targetInventory.contents.forEachIndexed { slot, item ->
            if (slot in 0..8) {
                viewerInventory.setItem(slot + 45, safeItem(item))
            } else {
                viewerInventory.setItem(slot + 9, safeItem(item))
            }
        }

        viewerInventory.setItem(0, safeItem(getCursor()))
        viewerInventory.setItem(2, safeItem(targetInventory.helmet))
        viewerInventory.setItem(3, safeItem(targetInventory.chestplate))
        viewerInventory.setItem(4, safeItem(targetInventory.leggings))
        viewerInventory.setItem(5, safeItem(targetInventory.boots))

        val craftingInventory = (nmsTarget.defaultContainer as ContainerPlayer).craftInventory
        VIEW_CRAFTING.forEachIndexed { slot, viewSlot ->
            viewerInventory.setItem(viewSlot, safeItem(craftingInventory.getItem(slot)))
        }
    }

    fun setItemInInv(viewSlot: Int, item: ItemStack?) {
        when (viewSlot) {
            in VIEW_INVENTORY -> targetInventory.setItem(viewSlot - 9, safeItem(item))
            in VIEW_HOTBAR -> targetInventory.setItem(viewSlot - 45, safeItem(item))
            in VIEW_ARMOR -> setArmorSlot(viewSlot, item)
            in VIEW_CRAFTING -> setCraftingSlot(viewSlot, item)
            VIEW_CURSOR -> setCursor(item)
        }
    }

    fun setArmorSlot(viewSlot: Int, item: ItemStack?) {
        when (viewSlot) {
            2 -> targetInventory.helmet = safeItem(item)
            3 -> targetInventory.chestplate = safeItem(item)
            4 -> targetInventory.leggings = safeItem(item)
            5 -> targetInventory.boots = safeItem(item)
        }
    }

    fun setCraftingSlot(viewSlot: Int, item: ItemStack?) {
        val craftingInventory = (nmsTarget.defaultContainer as ContainerPlayer).craftInventory
        when (viewSlot) {
            7 -> craftingInventory.setItem(0, toNmsItem(safeItem(item)))
            8 -> craftingInventory.setItem(1, toNmsItem(safeItem(item)))
            16 -> craftingInventory.setItem(2, toNmsItem(safeItem(item)))
            17 -> craftingInventory.setItem(3, toNmsItem(safeItem(item)))
        }
    }

    fun getCursor(): ItemStack = CraftItemStack(nmsTarget.inventory.j())

    fun setCursor(item: ItemStack?) {
        nmsTarget.inventory.b(toNmsItem(safeItem(item)))
        nmsTarget.z()
    }

    fun handleClick(packet: Packet102WindowClick) {
        if (nmsViewer.activeContainer.windowId != packet.a || !nmsViewer.activeContainer.c(nmsViewer))
            return

        val slot = packet.b
        val allow = if (slot in VIEW_ALL) {
            viewer.hasPermission("zcore.invsee.edit") && slot !in VIEW_EMPTY
        } else {
            !packet.f
        }

        var itemStack: net.minecraft.server.ItemStack? = null
        if (allow) {
            itemStack = nmsViewer.activeContainer.a(packet.b, packet.c, packet.f, nmsViewer)
        }

        if (allow && net.minecraft.server.ItemStack.equals(packet.e, itemStack)) {
            nmsViewer.netServerHandler.sendPacket(Packet106Transaction(packet.a, packet.d, true))
            nmsViewer.h = true
            nmsViewer.activeContainer.a()
            nmsViewer.z()
            nmsViewer.h = false

            if (slot in VIEW_ALL) {
                setItemInInv(slot, safeItem(viewerInventory.getItem(slot)))
            }
        } else {
            val n = getField<HashMap<Any, Any>>(nmsViewer.netServerHandler, "n")
            n.put(nmsViewer.activeContainer.windowId, packet.d)
            nmsViewer.netServerHandler.sendPacket(Packet106Transaction(packet.a, packet.d, false))
            nmsViewer.activeContainer.a(nmsViewer, false)

            val list = mutableListOf<net.minecraft.server.ItemStack?>()
            for (slot in nmsViewer.activeContainer.e) {
                list.add((slot as Slot).item)
            }

            nmsViewer.a(nmsViewer.activeContainer, list)
        }
    }

    companion object {
        val VIEW_ALL: IntRange = 0..53
        val VIEW_INVENTORY: IntRange = 18..44
        val VIEW_HOTBAR: IntRange = 45..53
        val VIEW_ARMOR: IntRange = 2..5
        val VIEW_CRAFTING: List<Int> = listOf(7, 8, 16, 17)
        val VIEW_CURSOR: Int = 0
        val VIEW_EMPTY: List<Int> = listOf(1, 6) + (9..15)

        fun safeItem(item: ItemStack?): ItemStack? =
            if (item != null && item.typeId == 0) null else item

        fun safeItem(item: net.minecraft.server.ItemStack?): ItemStack? =
            if (item == null || item.id == 0) null
            else ItemStack(item.id, item.count, item.damage.toShort())

        fun toNmsItem(item: ItemStack?): net.minecraft.server.ItemStack? =
            if (item == null) null
            else net.minecraft.server.ItemStack(item.typeId, item.amount, item.durability.toInt())
    }

    private class NmsInventory(val playerName: String) : IInventory {

        val items: Array<net.minecraft.server.ItemStack?> = arrayOfNulls(54)

        override fun getSize(): Int = items.size

        override fun getItem(i: Int): net.minecraft.server.ItemStack? = items[i]

        override fun splitStack(i: Int, j: Int): net.minecraft.server.ItemStack? {
            val item = items[i] ?: return null
            val retItem: net.minecraft.server.ItemStack

            if (item.count <= j) {
                retItem = item
                items[i] = null
                return retItem
            } else {
                retItem = item.a(j)
                if (item.count == 0) {
                    items[i] = null
                }

                return retItem
            }
        }

        override fun setItem(i: Int, itemStack: net.minecraft.server.ItemStack?) {
            if (itemStack != null && itemStack.count > 64) {
                itemStack.count = 64
            }
            items[i] = itemStack
        }

        override fun getName(): String = local("command.invsee.title", playerName)

        override fun getMaxStackSize(): Int = 64

        override fun update() {}

        override fun a_(entityHuman: EntityHuman?): Boolean = true

        override fun getContents(): Array<net.minecraft.server.ItemStack?> = items

    }

}