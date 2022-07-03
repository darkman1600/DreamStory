package com.dreamstory.ability.core

import kr.dreamstory.ability.ability.guiMap
import kr.dreamstory.ability.ability.main
import com.dreamstory.library.coroutine.schedule
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class GUI(
    p: Player,
    val size: Int,
    private val title: String,
    vararg var objs: Any?,
) {

    inline fun <reified T> getObject(index: Int): T? {
        return try {
            objs[index] as T
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> getObjectFx(index: Int): T = objs[index] as T

    companion object {
        private val air by lazy { ItemStack(Material.AIR) }
        private val void by lazy {
            val temp = ItemStack(Material.RABBIT_HIDE)
            val meta = temp.itemMeta
            meta.setCustomModelData(1)
            meta.setDisplayName("§f")
            temp.itemMeta = meta
            temp
        }

        private val reset by lazy {
            val item = ItemStack(Material.RABBIT_HIDE)
            val meta = item.itemMeta
            meta.setCustomModelData(2)
            item.itemMeta = meta
            item
        }

        private val question by lazy {
            val item = ItemStack(Material.RABBIT_HIDE)
            val meta = item.itemMeta
            meta.setCustomModelData(3)
            item.itemMeta = meta
            item
        }
    }

    val inv by lazy { main.server.createInventory(null, size, title) }

    private val uniqueId: UUID = p.uniqueId
    var player: Player? = null
        private set
        get() = main.server.getPlayer(uniqueId)

    init {
        firstInit()
        init()
        open()
    }

    private fun open() {
        main.server.scheduler.schedule(main) {
            player?.closeInventory()
            waitFor(1)
            guiMap[uniqueId] = this@GUI
            player?.openInventory(inv) ?: guiMap.remove(uniqueId)
        }
    }

    fun refresh(clear: Boolean = false) {
        main.server.scheduler.schedule(main) {
            repeating(1)
            if (clear) inv.clear()
            init()
        }
    }

    fun setItem(title: String, des: List<String>?, material: Material, slot: Int, customModelData: Int = 0, amount: Int = 1, glow: Boolean = false) {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta
        meta.setDisplayName(title)
        if(des!=null) meta.lore = des
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        if(customModelData != 0) meta.setCustomModelData(customModelData)
        item.itemMeta = meta
        if(glow) item.addUnsafeEnchantment(Enchantment.LURE, 1)
        inv.setItem(slot, item)
    }

    fun setVoidSlot(slot: Int) {
        inv.setItem(slot, void)
    }

    fun setAirSlot(slot: Int) {
        inv.setItem(slot, air)
    }

    fun setResetButton(slot: Int,name: String,lore: ArrayList<String>?) {
        val item = reset.clone()
        val meta = item.itemMeta
        meta.setDisplayName(name)
        if(lore != null) meta.lore = lore
        inv.setItem(slot, item)
    }

    fun setQuestionMark(slot: Int,name: String,lore: ArrayList<String>?) {
        val item = question.clone()
        val meta = item.itemMeta
        meta.setDisplayName(name)
        if(lore != null) meta.lore = lore
        inv.setItem(slot, item)
    }

    open fun firstInit() {}
    abstract fun init()
    abstract fun InventoryClickEvent.clickEvent()
    abstract fun InventoryCloseEvent.closeEvent()
    abstract fun InventoryDragEvent.dragEvent()

    fun guiClick(e: InventoryClickEvent) { e.clickEvent() }
    fun guiClose(e: InventoryCloseEvent) { guiMap.remove(e.player.uniqueId); e.closeEvent() }
    fun guiDrag(e: InventoryDragEvent) { e.dragEvent() }

}