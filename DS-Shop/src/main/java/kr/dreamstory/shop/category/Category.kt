package kr.dreamstory.shop.category

import kr.dreamstory.shop.prize.Prize
import org.bukkit.inventory.ItemStack

class Category(
    val key: String,
    val slot: Int,
    val item: ItemStack,
    val prizes: List<Prize>
) {

    val icon = item

}