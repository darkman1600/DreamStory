package kr.dreamstory.library.gui.type

import kr.dreamstory.library.item.dreamstory.api.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

interface PageAble {

    fun getItemList(): List<ItemStack>
    fun getPage(): Int
    fun setPage(page: Int)
    fun getPrevSlot(): Int
    fun getNextSlot(): Int
    fun getEmptySlot(): Collection<Int>

    companion object {
        private val prevButton by lazy {
            ItemStackBuilder.build(Material.PAPER,"§f이전 페이지", listOf("§7이전 페이지로 이동합니다."))
        }

        private val nextButton by lazy {
            ItemStackBuilder.build(Material.PAPER,"§f다음 페이지", listOf("§7다음 페이지로 이동합니다."))
        }
    }

    fun setPageButton(inv: Inventory) {
        inv.setItem(getPrevSlot(), prevButton)
        inv.setItem(getNextSlot(), nextButton)
    }

    fun turn(inv: Inventory, turnNext: Boolean) {
        var currentPage = getPage()
        if(turnNext) currentPage ++
        else if(currentPage > 0) currentPage --
        val emptySlotSize = getEmptySlot().size
        var startPoint = (inv.size - (2 + emptySlotSize)) * currentPage
        val itemList = getItemList()
        if(itemList.isEmpty()) {
            inv.close()
            return
        }
        fun getStartPoint() {
            if(itemList.getOrNull(startPoint) == null) {
                startPoint -= inv.size - (2 + emptySlotSize)
                currentPage --
                getStartPoint()
            }
        }
        getStartPoint()
        setPage(currentPage)

        val iter = itemList.listIterator(startPoint)
        val numberList = (0 until inv.size - 1).toMutableList()
        numberList.remove(getNextSlot())
        numberList.remove(getPrevSlot())
        numberList.removeAll(getEmptySlot())
        numberList.forEach {
            if(iter.hasNext()) inv.setItem(it,iter.next())
        }
    }

    fun turnPage(slot: Int, inv: Inventory) {
        when(slot) {
            getPrevSlot() -> turn(inv,false)
            getNextSlot() -> turn(inv,true)
        }
    }

}