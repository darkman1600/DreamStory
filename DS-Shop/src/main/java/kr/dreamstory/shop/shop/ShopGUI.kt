package kr.dreamstory.shop.shop

import kr.dreamstory.library.gui.DSGUI
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class ShopGUI(private val shopData: ShopData): DSGUI(shopData.size, shopData.title) {

    init {

    }

    override fun init() {}

    override fun InventoryClickEvent.clickEvent() {
    }

    override fun InventoryCloseEvent.closeEvent() {
    }

    override fun InventoryDragEvent.dragEvent() {
    }

}