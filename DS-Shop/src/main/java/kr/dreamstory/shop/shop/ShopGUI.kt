package kr.dreamstory.shop.shop

import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.item.minecraft.api.ItemStackBuilder
import kr.dreamstory.library.item.minecraft.setGlow
import kr.dreamstory.shop.category.Category
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class ShopGUI(private val shopData: ShopData): DSGUI(shopData.size, shopData.title) {

    private var currentCategory = shopData.categories.first()
    private val voidIcon = ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE,"")

    init {
        shelveVoidSlots()
        shelveCategories()
        shelvePrizes(currentCategory)
    }

    override fun init() {}

    private fun clearShelf() {
        val shelves = (0 until shopData.size) - shopData.categorySlots.toSet() - shopData.voidSlots.toSet()
        shelves.forEach { setAirSlot(it) }
    }

    private fun shelveCategories() {
        val categories = shopData.categories.iterator()
        for (slot in shopData.categorySlots) {
            if (!categories.hasNext()) break
            val category = categories.next()
            val icon = if (category == currentCategory) category.icon.setGlow() else category.icon
            setItem(slot, icon)
        }
    }

    private fun shelvePrizes(category: Category) {
        currentCategory = category
        val prizes = category.prizes.iterator()
        for (slot in shopData.prizeSlots) {
            if (!prizes.hasNext()) break
            val prize = prizes.next().icon
            setItem(slot, prize)
        }
    }

    private fun shelveVoidSlots() {
        shopData.voidSlots.forEach { setItem(it,voidIcon) }
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        when(rawSlot) {
            in shopData.categorySlots -> {
                val category = shopData.categories.getOrNull(shopData.categorySlots.indexOf(rawSlot))?: return
                if (category == currentCategory) return else currentCategory = category
                clearShelf()
                shelveCategories()
                shelvePrizes(category)
            }
            in shopData.prizeSlots -> {
                val prize = currentCategory.prizes.getOrNull(shopData.prizeSlots.indexOf(rawSlot))?: return
                if (prize.item == null) return
                val player = whoClicked as Player
                if (isLeftClick) {
                    if (prize.price.toBuy == null) return
                    if (!isShiftClick) shopData.buy(player, prize,1)
                    else if (prize.price.stackable) shopData.buy(player, prize,64)
                } else if (isRightClick) {
                    if (prize.price.toSell == null) return
                    if (!isShiftClick) shopData.sell(player, prize, 1)
                    else if (prize.price.stackable) shopData.sell(player, prize, 64)
                }
            }
        }

    }

    override fun InventoryCloseEvent.closeEvent() {}

    override fun InventoryDragEvent.dragEvent() { isCancelled = true }

}