package kr.dreamstory.shop.shop

import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.shop.category.Category
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class ShopGUI(private val shopData: ShopData): DSGUI(shopData.size, shopData.title) {

    private lateinit var currentCategory: Category

    init {
        shelvePrizes(shopData.categories.first())
        shelveCategories()
        shelveEmptySlots()
    }

    override fun init() {}

    private fun clearShelf() {
        val shelves = (0 until shopData.size) - shopData.categorySlots.keys.toSet() - shopData.emptySlots.toSet()
        shelves.forEach { setAirSlot(it) }
    }

    private fun shelveCategories() {
        shopData.categorySlots.forEach { (slot, category) ->
            setItem(slot, category.icon)
        }
    }

    private fun shelvePrizes(category: Category) {
        currentCategory = category
        category.prizeSlots.forEach { (slot, prize) ->
            setItem(slot, prize.icon)
        }
    }

    private fun shelveEmptySlots() {
        shopData.emptySlots.forEach {
            setVoidSlot(it)
        }
    }

    override fun InventoryClickEvent.clickEvent() {
        if (rawSlot >= shopData.size ) return
        isCancelled = true
        if (!isLeftClick) return
        when(rawSlot) {
            in shopData.categorySlots -> {
                clearShelf()
                val category = shopData.categorySlots[rawSlot]!!
                shelvePrizes(category)
            }
            in currentCategory.prizeSlots.keys -> {
                val prize = currentCategory.prizeSlots[rawSlot]!!
                shopData.buy(whoClicked as Player, prize)
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {
    }

    override fun InventoryDragEvent.dragEvent() {
        if (rawSlots.minOf { it >= shopData.size }) return
        isCancelled = true
    }

}