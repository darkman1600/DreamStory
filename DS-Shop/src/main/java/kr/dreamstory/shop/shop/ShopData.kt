package kr.dreamstory.shop.shop

import kr.dreamstory.library.extension.database
import kr.dreamstory.library.extension.economy
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.main
import kr.dreamstory.shop.prize.PriceType
import kr.dreamstory.shop.prize.Prize
import org.bukkit.entity.Player

class ShopData(
    val key: String,
    val size: Int,
    val title: String,
    val emptySlots: List<Int>,
    val categories: List<Category>
) {
    val categorySlots = HashMap<Int,Category>()

    init {
        emptySlots.forEach { slot ->
            if (slot < 0 || slot >= size) {
                MessageManager.pluginMessage(main,"shopData_emptySlot 범위 오류 : ${key}.emptySlot")
                return@forEach
            }
        }
        if (categories.isEmpty()) MessageManager.pluginMessage(main,"shopData_category 카테고리를 찾을 수 없음 : ${key}.category")
        categories.forEach { category ->
            val categorySlot = category.slot
            if (categorySlot < 0 || categorySlot >= size) {
                MessageManager.pluginMessage(main,"shopData_category_slot 범위 오류 : ${key}.$category.slot($categorySlot)")
                return@forEach
            }
            categorySlots[categorySlot] = category
            category.prizes.forEach { prize ->
                val prizeSlot = prize.slot
                if (prizeSlot < 0 || prizeSlot >= size) {
                    MessageManager.pluginMessage(main,"shopData_prize_slot 범위 오류 : ${key}.$category.$prize.slot($prizeSlot)")
                    return@forEach
                }
                category.prizeSlots[prize.slot] = prize
            }
        }
    }

    fun buy(player: Player, prize: Prize): Boolean {
        when (prize.price.type) {
            PriceType.MONEY ->
            PriceType.MONEY
        }
        player.economy
        player.sendMessage("§bBUY SUCCESS!!!!!!! §7- §6${prize.item.displayName}")
        return true
    }

}