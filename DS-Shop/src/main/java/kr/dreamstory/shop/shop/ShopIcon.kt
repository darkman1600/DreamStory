package kr.dreamstory.shop.shop

import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopIcon(private val itemKey: String, val slot: Int) {

    fun getItem(): ItemStack {
        val dsItem = DSItemManager.getDSItem(itemKey)
            ?: DSItemManager.getNewDSItem("Null", ItemStack(Material.RABBIT_HIDE),false)
        return dsItem.itemStack
    }

}