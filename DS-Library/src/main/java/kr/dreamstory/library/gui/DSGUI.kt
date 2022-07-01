package com.dreamstory.library.gui

import com.dreamstory.library.coroutine.schedule
import com.dreamstory.library.main
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

abstract class DSGUI(
    val size: Int,
    private val title: String,
    vararg var objs: Any?,
) {

    inline fun <reified T>getObject(index: Int): T? {
        return try {
            objs[index] as T
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> getObjectFx(index: Int): T = objs[index] as T

    companion object {
        val air by lazy { ItemStack(Material.AIR) }
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

    init {
        firstInit()
        init()
    }

    fun open(player: Player) {
        main.server.scheduler.schedule(main) {
            player.closeInventory()
            val uuid = player.uniqueId
            waitFor(1)

            val event = DSGUIOpenEvent(player,this@DSGUI)
            main.server.pluginManager.callEvent(event)
            if(event.isCancelled) return@schedule

            DSGUIManager.inputPlayerGUI(uuid, this@DSGUI)
            if(player.isOnline) {
                player.sendMessage("a")
                player.openInventory(this@DSGUI.inv) ?: DSGUIManager.removePlayerGUI(uuid)
            }
        }
    }

    fun refresh(clear: Boolean = false) {
        if (clear) inv.storageContents = arrayOf()
        init()
    }

    fun ItemStack?.isEmpty(): Boolean = this == null || this.type == Material.AIR

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
    fun setItem(slot: Int, itemStack: ItemStack) {
        inv.setItem(slot,itemStack)
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

    fun getPlayerHead(player: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.owningPlayer = player
        item.itemMeta = meta
        return item
    }

    open fun firstInit() {}
    abstract fun init()
    abstract fun InventoryClickEvent.clickEvent()
    abstract fun InventoryCloseEvent.closeEvent()
    abstract fun InventoryDragEvent.dragEvent()

    fun guiClick(e: InventoryClickEvent) { e.clickEvent() }
    fun guiClose(e: InventoryCloseEvent) {
        DSGUIManager.removePlayerGUI(e.player.uniqueId); e.closeEvent() }
    fun guiDrag(e: InventoryDragEvent) { e.dragEvent() }

}