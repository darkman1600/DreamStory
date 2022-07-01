package kr.dreamstory.library.item.dreamstory.item.objs

import kr.dreamstory.library.item.dreamstory.item.DSItemStack
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemTier
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import org.bukkit.inventory.ItemStack

class DSUpgradeStone(
    key: String,
    itemStack: ItemStack,
    canTrade: Boolean,
    tier: DSItemTier,
    uType: DSItemType
): DSItemStack(
    key,
    itemStack,
    DSItemType.UPGRADE_STONE,
    canTrade,
    tier
) {

    val upgradeType = uType

}