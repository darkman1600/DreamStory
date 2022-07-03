package com.dreamstory.ability.extension

import kr.dreamstory.ability.ability.play.item.DropGrade
import kr.dreamstory.ability.ability.play.item.DropItemData
import com.dreamstory.ability.manager.DropItemManager
import com.dreamstory.ability.util.getNBTTagCompound
import com.dreamstory.ability.util.percent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.stream.Collectors

fun String.toDropGrade(): DropGrade? = Arrays.stream(DropGrade.values()).filter { it.label.equals(this, true) }.collect(Collectors.toList())[0]
fun Int.toDropGrade(): DropGrade? = Arrays.stream(DropGrade.values()).filter { it.index == this }.collect(Collectors.toList())[0]

val ItemStack.isDropItem: Boolean
    get() {
        this.getNBTTagCompound(DropItemData::class.java)?: return false
        return true
    }

var ItemStack.grade: DropGrade?
    get() {
        val temp = getNBTTagCompound(DropItemData::class.java)?: return null
        return temp.grade
    }
    set(value) {
        if (value != null) { itemMeta = DropItemManager.setGrade(this, value).itemMeta }
    }

fun ItemStack.naturalDrop(p: Player): ItemStack {
    grade = DropGrade.FINEST
    return naturalDrop(p, 0.0)
}

fun ItemStack.naturalDrop(p: Player, plus: Double): ItemStack {
    val result = clone()
    var max = false
    var pp = plus
    if(isDropItem) {
        for(i in 0 until 4) {
            if(i == 3) pp = 0.0
            if((DropItemManager.minePercent[i] + pp).percent()) {
                if(i == 3) {
                    result.grade = 4.toDropGrade()
                    max = true
                }
            } else {
                result.grade = i.toDropGrade()
                break
            }
        }
    }
    if(p.inventory.addItem(result).isEmpty()) {
        // max Inventory
    } else max = false
    if(max) {

    }
    return result
}

