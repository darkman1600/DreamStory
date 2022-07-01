package kr.dreamstory.library.item.dreamstory.item

import kr.dreamstory.library.item.dreamstory.item.enums.DSItemTier
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import org.bukkit.inventory.ItemStack

abstract class DSItemStack(
    val key: String,
    val itemStack: ItemStack,
    val type: DSItemType,
    val canTrade: Boolean = true,
    val tier: DSItemTier
) {

}