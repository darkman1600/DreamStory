package kr.dreamstory.shop.shop

import kr.dreamstory.library.item.dreamstory.data.DSItemDataManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopIcon(private val itemKey: String, val slot: Int) {

    fun getItem(): ItemStack {
        val dsItem = DSItemDataManager.getDSItem(itemKey)
            ?: DSItemDataManager.getNewDSItem("Null", ItemStack(Material.RABBIT_HIDE),false)
        return dsItem.itemStack
    }

}