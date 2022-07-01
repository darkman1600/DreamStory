package com.dreamstory.library.item.dsitem.objs

import org.bukkit.inventory.ItemStack

abstract class DSItemStack(
    val key: String,
    val itemStack: ItemStack,
    val type: DSItemType,
    val canTrade: Boolean = true,
    val tier: DSItemTier
) {

}