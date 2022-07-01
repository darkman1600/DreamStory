package kr.dreamstory.library.item.dreamstory.item.objs

import kr.dreamstory.library.item.dreamstory.item.DSItemStack
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemTier
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import org.bukkit.inventory.ItemStack

class DSDefaultItem(
    key: String,
    itemStack: ItemStack,
    canTrade: Boolean,
    tier: DSItemTier
): DSItemStack(
    key,
    itemStack,
    DSItemType.DEFAULT,
    canTrade,
    tier
) {

}