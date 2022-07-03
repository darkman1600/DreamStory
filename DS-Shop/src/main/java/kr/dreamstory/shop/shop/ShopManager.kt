package kr.dreamstory.shop.shop

import kr.dreamstory.shop.DataLoader
import kr.dreamstory.shop.category.Category
import org.bukkit.entity.Player

object ShopManager {

    val categoryMap = HashMap<String,Category>()
    val shopMap = HashMap<String, ShopData>()

    fun openGui(player: Player, shopData: ShopData) {
        val gui = ShopGUI(shopData)
        gui.open(player)
    }
}