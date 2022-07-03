package kr.dreamstory.shop.shop

import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.main
import kr.dreamstory.shop.prize.Prize
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class ShopGUI(private val shopData: ShopData): DSGUI(shopData.size, shopData.title) {

    private lateinit var currentCategory: Category

    private val emptySlots = ArrayList<Int>()
    private val categorySlots = HashMap<Int,Category>()
    private val prizeSlots = HashMap<Category, HashMap<Int,Prize>>()

    init {
        shopData.emptySlot.forEach { slot ->
            if (slot < 0 || slot >= size) {
                MessageManager.pluginMessage(main,"shopData_emptySlot 범위 오류 : ${shopData.key}.emptySlot")
                return@forEach
            }
            emptySlots.add(slot)
        }
        if (shopData.categories.isEmpty()) MessageManager.pluginMessage(main,"shopData_category 카테고리를 찾을 수 없음 : ${shopData.key}.category")
        shopData.categories.forEach { category ->
            val categorySlot = category.slot
            if (categorySlot < 0 || categorySlot >= size) {
                MessageManager.pluginMessage(main,"shopData_category_slot 범위 오류 : ${shopData.key}.$category.slot($categorySlot)")
                return@forEach
            }
            categorySlots[categorySlot] = category
            prizeSlots[category] = hashMapOf()
            category.prizes.forEach { prize ->
                val prizeSlot = prize.slot
                if (prizeSlot < 0 || prizeSlot >= size) {
                    MessageManager.pluginMessage(main,"shopData_prize_slot 범위 오류 : ${shopData.key}.$category.$prize.slot($prizeSlot)")
                    return@forEach
                }
                prizeSlots[category]!![prizeSlot] = prize
            }
        }
        shelvePrizes(shopData.categories.first())
        shelveCategories()
        shelveEmptySlots()
    }

    override fun init() {}

    private fun clearShelf() {
        val shelves = (0 until shopData.size) - categorySlots.keys.toSet() - emptySlots.toSet()
        shelves.forEach { setAirSlot(it) }
    }

    private fun shelveCategories() {
        categorySlots.forEach { (slot, category) ->
            setItem(slot, category.icon)
        }
    }

    private fun shelvePrizes(category: Category) {
        currentCategory = category
        (prizeSlots[category]?: return).forEach { (slot, prize) ->
            setItem(slot, prize.icon)
        }
    }

    private fun shelveEmptySlots() {
        emptySlots.forEach {
            setVoidSlot(it)
        }
    }

    override fun InventoryClickEvent.clickEvent() {
        if (rawSlot >= shopData.size ) return
        isCancelled = true
        if (!isLeftClick) return
        when(rawSlot) {
            in categorySlots -> {
                clearShelf()
                val category = categorySlots[rawSlot]!!
                shelvePrizes(category)
            }
            in (prizeSlots[currentCategory]?: return).keys -> {
                val prize = prizeSlots[currentCategory]!![rawSlot]?: return
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