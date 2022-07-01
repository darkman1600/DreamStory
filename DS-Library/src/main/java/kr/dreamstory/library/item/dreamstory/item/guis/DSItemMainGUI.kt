package kr.dreamstory.library.item.dreamstory.item.guis

import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.item.minecraft.api.ItemStackBuilder
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class DSItemMainGUI: DSGUI(27,"§f항목을 선택하세요") {

    override fun init() {
        for(slot in 0..26) setVoidSlot(slot)
        DSItemType.values().forEach {
            setItem(it.slot, ItemStackBuilder.buildHideFlags(it.material,"§e${it.display}", listOf("§f아이템 목록을 확인합니다."),null))
        }

    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val type = DSItemType.getTypeFromSlot(rawSlot) ?: return
        val gui = DSItemListGUI(type).open(whoClicked as Player)
    }
    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}

}