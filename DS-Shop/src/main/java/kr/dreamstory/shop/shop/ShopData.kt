package kr.dreamstory.shop.shop

import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.prize.Prize
import org.bukkit.entity.Player

class ShopData(
    val key: String,
    val size: Int,
    val title: String,
    val emptySlot: List<Int>,
    val categories: List<Category>
) {

    init {
    }

    fun buy(player: Player, prize: Prize): Boolean {
        player.inventory.addItem(prize.item)
        player.sendMessage("§bBUY SUCCESS!!!!!!! §7- §6${prize.item.displayName}")
        return true
    }

}