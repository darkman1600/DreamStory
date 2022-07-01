package kr.dreamstory.library.item.dreamstory.item.objs

import kr.dreamstory.library.item.dreamstory.item.DSItemStack
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemTier
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import kr.dreamstory.library.item.dreamstory.item.interfaces.UpgradeAble
import kr.dreamstory.library.item.minecraft.getIntNbt
import kr.dreamstory.library.item.minecraft.setIntNbt
import kr.dreamstory.library.item.minecraft.setLores
import kr.dreamstory.library.item.minecraft.setName
import org.bukkit.inventory.ItemStack

class DSArmor(
    key: String,
    item: ItemStack,
    canTrade: Boolean,
    tier: DSItemTier,
    raw: String,
    des: List<String>
): DSItemStack(
    key,
    item,
    DSItemType.ARMOR,
    canTrade,
    tier
), UpgradeAble {

    val rawName = raw
    val description = des

    override fun getNextUpgradeItem(item: ItemStack): ItemStack {
        val clone = itemStack.clone()
        val level = getUpgradeLevel(item) + 1
        val newName = tier.color + rawName + "§a( + $level )"
        val lores = description + listOf("","§7${type.display}")
        clone.setName(newName)
        clone.setLores(lores)
        return clone
    }

    override fun getUpgradeCost(): Long {
        TODO("Not yet implemented")
    }

    override fun getUpgradePercent(): Double {
        TODO("Not yet implemented")
    }

    override fun getUpgradeLevel(item: ItemStack): Int = item.getIntNbt("DSUpgradeItemLevel")!!
    override fun setUpgradeLevel(item: ItemStack, level: Int) = item.setIntNbt("DSUpgradeItemLevel",level)

}