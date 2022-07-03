package kr.dreamstory.library.gui.type

import org.bukkit.inventory.Inventory

interface ScrollAble: PageAble {

    override fun turn(inv: Inventory, turnNext: Boolean) {
        var currentPage = getPage()
        if (turnNext) currentPage ++
        else if (currentPage > 0) currentPage --
        val slots = setOf(getPrevSlot(),getNextSlot()) + getEmptySlot()
        var startPoint = (9 - slots.filter { (0..8).contains(it) }.size) * currentPage
        val itemList = getItemList()
        if(itemList.isEmpty()) {
            inv.close()
            return
        }
        fun getStartPoint() {
            if(itemList.getOrNull(startPoint) == null) {
                startPoint -= (9 - slots.filter { (0..8).contains(it) }.size)
                currentPage --
                getStartPoint()
            }
        }
        getStartPoint()
        setPage(currentPage)

        val iter = itemList.listIterator(startPoint)
        val numberList = (0 until inv.size).toMutableList()
        numberList.remove(getPrevSlot())
        numberList.remove(getNextSlot())
        getEmptySlot().forEach { numberList.remove(it) }
        numberList.forEach {
            if(iter.hasNext()) inv.setItem(it,iter.next())
        }
    }

}