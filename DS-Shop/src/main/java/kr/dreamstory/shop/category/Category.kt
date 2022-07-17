package kr.dreamstory.shop.category

import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.item.minecraft.api.ItemStackBuilder
import kr.dreamstory.shop.prize.Prize
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Category(
    private val key: String,
    private val itemKey: String,
    val prizes: List<Prize>
) {

    val icon: ItemStack
        get() {
            val item = DSItemManager.getDSItem(itemKey)
                ?: DSItemManager.getNewDSItem("null", ItemStackBuilder.build(Material.RABBIT_HIDE,32),false)
            return item.itemStack.clone()
        }

}