package kr.dreamstory.library.item.dreamstory.item

import com.dreamstory.library.item.dsitem.objs.DSItemStack
import com.dreamstory.library.item.dsitem.objs.DSItemTier
import com.dreamstory.library.item.dsitem.objs.DSItemType
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