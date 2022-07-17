package kr.dreamstory.shop.prize

import kr.dreamstory.library.extension.addLore
import org.bukkit.inventory.ItemStack

class Prize(
    val key: String,
    val slot: Int,
    var item: ItemStack,
    val price: Price
) {
    val icon = item.addLore("§6가격 §7- §f${price.amount} §7원")

    init {

    }
}