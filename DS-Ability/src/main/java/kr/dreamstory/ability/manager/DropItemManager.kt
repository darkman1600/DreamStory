package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.play.item.DropGrade
import kr.dreamstory.ability.ability.play.item.DropItemData
import kr.dreamstory.ability.util.addNBTTagCompound
import kr.dreamstory.ability.util.getNBTTagCompound
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

object DropItemManager {

    // ++ Support by DropItemExtension
    val minePercent by lazy { arrayOf(10.0, 5.0, 3.0, .1) }
    val costMultiple by lazy { arrayOf(1.5,10.0,100.0,1000.0) }

    fun registerDropItem(item: ItemStack, cost: Int) { item.itemMeta = item.addNBTTagCompound(DropItemData(cost)).itemMeta }

    fun getCost(item: ItemStack): Int {
        val data = item.getNBTTagCompound(DropItemData::class.java)?: return 0
        val index = data.grade?.index?: 0
        val mul = if(index == 0) 1.0 else costMultiple[index - 1]
        return (data.cost.toDouble() * mul).roundToInt()
    }

    fun getCost(cost: Int, grade: DropGrade): Int {
        val index = grade.index
        val mul = if(index == 0) 1.0 else costMultiple[index-1]
        return (cost.toDouble() * mul).roundToInt()
    }

    fun setGrade(item: ItemStack, grade: DropGrade): ItemStack {
        val data = item.getNBTTagCompound(DropItemData::class.java)
        if(data == null) return item.clone()
        else return data.getItem(item.clone(),grade)
    }

}