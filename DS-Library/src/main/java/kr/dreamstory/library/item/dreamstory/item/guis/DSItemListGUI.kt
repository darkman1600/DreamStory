package kr.dreamstory.library.item.dreamstory.item.guis

import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.gui.type.PageAble
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import kr.dreamstory.library.main
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

class DSItemListGUI(val type: DSItemType): DSGUI(54,"§f서버 아이템 목록"), PageAble {

    private var currentPage = 0

    override fun getPrevSlot(): Int = 45
    override fun getNextSlot(): Int = 53
    override fun getEmptySlot(): List<Int> = listOf(46,47,48,49,50,51,52)
    override fun getPage(): Int = currentPage
    override fun setPage(page: Int) { currentPage = page }
    override fun getItemList(): List<ItemStack> = DSItemManager.getDSItemStackList().filter { it.type == type }.map { it.itemStack }

    override fun init() {
        setPageButton(inv)
        main.server.scheduler.schedule(main) {
            waitFor(1)
            turnPage(getPrevSlot(),inv)
        }
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val player = whoClicked as Player
        player.playSound(player.location, Sound.BLOCK_LEVER_CLICK,1F,2F)
        turnPage(rawSlot,clickedInventory ?: return)
    }
    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}
}