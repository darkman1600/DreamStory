package kr.dreamstory.shop

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.prize.Price
import kr.dreamstory.shop.prize.PriceType
import kr.dreamstory.shop.prize.Prize
import kr.dreamstory.shop.shop.ShopData
import kr.dreamstory.shop.shop.ShopManager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object DataLoader { // 여기서 안하더라도 중복, 범위 확인구문 필요

    fun load() {
        main.schedule(SynchronizationContext.ASYNC) {
            while (!DSItemManager.isLoaded) waitFor(5)
            ShopManager.categoryMap.clear()
            ShopManager.shopMap.clear()
            if (!loadDirectory("category") { loadCategory(it) }) pluginMessage("§acategory §f디렉토리 로드 실패.")
            if (!loadDirectory("shopData") { loadShopData(it) }) pluginMessage("§ashopData §f디렉토리 로드 실패.")
        }
    }

    private inline fun loadDirectory(path: String, func: YamlConfiguration.(String)-> Boolean): Boolean {
        val directory = File("${main.dataFolder.path}\\database\\$path")
        if (!directory.exists()) {
            try { directory.mkdirs() }
            catch (e: Exception) { e.printStackTrace() }
            return false
        }
        var count = 0
        (directory.listFiles()?: return false).forEach { file ->
            val config = YamlConfiguration.loadConfiguration(file)
            config.getKeys(false).forEach { if (config.func(it)) count ++ }
        }
        pluginMessage("§f총 §a$count §f개의 $path 로드 완료.")
        return true
    }

    private fun YamlConfiguration.loadCategory(key: String): Boolean {
        val categorySlot = getString("$key.slot")?.toIntOrNull()?: run { pluginMessage("$key.§aslot §f값 없음."); return false }
        val categoryItemKey = getString("$key.itemKey")?: run { pluginMessage("$key.§aitemKey §f값 없음."); return false }
        val categoryItem = DSItemManager.getDSItem(categoryItemKey)?.itemStack?: getNullItem()
        val prizes = getList("$key.prize") { prize ->
            val prizeSlot = getString("$key.prize.$prize.slot")?.toIntOrNull()?: run { pluginMessage("$key.$prize.§aslot §f값 없음."); return false }
            val prizeItemKey = getString("$key.prize.$prize.itemKey")?: run { pluginMessage("$key.$prize.§aitemKey §f값 없음."); return false }
            val prizeItem = DSItemManager.getDSItem(prizeItemKey)?.itemStack?: getNullItem()
            val price = getString("$key.prize.$prize.price")?.toIntOrNull()?: run { pluginMessage("$key.$prize.§aprice §f값 없음."); return false }
            Prize(prize, prizeSlot, prizeItem.clone(), Price(price,PriceType.MONEY))
        }
        val category = Category(key, categorySlot, categoryItem.clone(), prizes)
        ShopManager.categoryMap[key] = category
        return true
    }

    private fun YamlConfiguration.loadShopData(key: String): Boolean {
        val size = getString("$key.size")?.toIntOrNull()?: run { pluginMessage("$key.§asize §f값 없음."); return false }
        val title = getString("$key.title")?: run { pluginMessage("$key.§atitle §f값 없음."); return false }
        val emptySlot = getIntegerList("$key.emptySlot")
        val categories = arrayListOf<Category>()
        getStringList("$key.category").forEach {
            val category = ShopManager.categoryMap[it]?: run { pluginMessage("$key.category.§a$it §f값 오류."); return false }
            categories.add(category)
        }
        val shopData = ShopData(key, size, title, emptySlot, categories)
        ShopManager.shopMap[key] = shopData
        return true
    }

    private inline fun <reified T> YamlConfiguration.getList(path: String, func: (key: String) -> T): List<T> {
        val list = arrayListOf<T>()
        val keys = if (path.isEmpty()) getKeys(false) else getConfigurationSection(path)?.getKeys(false)
        keys?.forEach { key -> list.add(func(key)) }
        return list
    }

    private fun pluginMessage(message: String) { MessageManager.pluginMessage(main,message) }

}