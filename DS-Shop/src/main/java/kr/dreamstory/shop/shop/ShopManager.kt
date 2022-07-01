package kr.dreamstory.shop.shop

object ShopManager {

    private var shopMap = HashMap<String, ShopData>()
    fun inputShop(key: String, shopData: ShopData) { shopMap[key] = shopData }
    fun clearShop() { shopMap = HashMap() }

}