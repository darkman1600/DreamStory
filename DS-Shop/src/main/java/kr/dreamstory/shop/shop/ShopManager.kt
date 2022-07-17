package kr.dreamstory.shop.shop

import kr.dreamstory.shop.DataLoader
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.prize.Prize
import org.bukkit.entity.Player
import kotlin.collections.HashMap

object ShopManager {

    val prizeMap = HashMap<String, Prize>()
    val categoryMap = HashMap<String, Category>()
    val shopMap = HashMap<String, ShopData>()

    fun openGui(player: Player, shopData: ShopData) {
        val gui = ShopGUI(shopData)
        gui.open(player)
    }

    fun reload() {
        shopMap.clear()
        categoryMap.clear()
        prizeMap.clear()
        DataLoader.load()
    }

}