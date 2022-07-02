package kr.dreamstory.ability.ability.play.item

import com.dreamstory.ability.manager.DropItemManager
import com.dreamstory.ability.util.addNBTTagCompound
import com.dreamstory.ability.util.integerFormat
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

data class DropItemData(
    val cost: Int,
    var grade: DropGrade? = null
) {

    fun getItem(item: ItemStack,grade: DropGrade?): ItemStack {
        if(grade == null) return item
        this.grade = grade
        val meta = item.itemMeta
        if(meta.hasDisplayName()) meta.setDisplayName("${grade.color}${ChatColor.stripColor(meta.displayName)}")
        if(meta.hasLore()) {
            val lore = ArrayList<String>()
            meta.lore!!.forEach {
                lore.add(it.replace("{grade}","${grade.color}${grade.label}").replace("{cost}","${DropItemManager.getCost(cost,grade).integerFormat()}"))
            }
            meta.lore = lore
        } else meta.lore = arrayListOf("§9전리품")
        item.itemMeta = meta
        return item.addNBTTagCompound(this)
    }

}
