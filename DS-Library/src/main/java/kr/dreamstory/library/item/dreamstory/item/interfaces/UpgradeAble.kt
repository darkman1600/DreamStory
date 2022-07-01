package kr.dreamstory.library.item.dreamstory.item.interfaces

import org.bukkit.inventory.ItemStack

interface UpgradeAble {

    fun getUpgradeCost(): Long
    fun getUpgradePercent(): Double
    fun getUpgradeLevel(item: ItemStack): Int
    fun setUpgradeLevel(item: ItemStack,level: Int)
    fun getNextUpgradeItem(item: ItemStack): ItemStack

}