package kr.dreamstory.shop

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.economy.PaymentType
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.prize.Price
import kr.dreamstory.shop.prize.Prize
import kr.dreamstory.shop.shop.ShopData
import kr.dreamstory.shop.shop.ShopManager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object DataLoader {

    fun load() {
        main.schedule(SynchronizationContext.ASYNC) {
            while (!DSItemManager.isLoaded) waitFor(5)
            if (!loadDirectory("prize") { loadPrize(it) }) pluginMessage("§aprize §f디렉토리 로드 실패.")
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

    private fun YamlConfiguration.loadPrize(key: String): Boolean {
        if (ShopManager.prizeMap.contains(key)) { pluginMessage("prize.${key} 값 중복."); return false }
        val itemKey = getString("$key.itemKey")?: run { pluginMessage("prize.$key.§aitemKey §f값 없음."); return false }
        val typeTag = getString("$key.price.type")?.uppercase()?: run { pluginMessage("prize.$key.price.§atype §f값 없음."); return false }
        val paymentType = PaymentType.fromString(typeTag)?: run { pluginMessage("prize.$key.price.§atype §f값 오류."); return false }
        val toBuy = getString("$key.price.buy")?.toLongOrNull()
        val toSell = getString("$key.price.sell")?.toLongOrNull()
        val stackable = getBoolean("$key.price.stackable")
        val price = Price(paymentType, toBuy, toSell, stackable)
        ShopManager.prizeMap[key] = Prize(key, itemKey, price)
        return true
    }

    private fun YamlConfiguration.loadCategory(key: String): Boolean {
        if (ShopManager.categoryMap.contains(key)) { pluginMessage("category.${key} 값 중복."); return false }
        val itemKey = getString("$key.itemKey")?: run { pluginMessage("category.$key.§aitemKey §f값 없음."); return false }
        val prizes = arrayListOf<Prize>()
        val prizeKeys = getStringList("$key.prize")
        if (prizeKeys.isEmpty()) { pluginMessage("category.$key.§aprize §f값 없음"); return false }
        prizeKeys.forEach {
            val prize = ShopManager.prizeMap[it]?: run { pluginMessage("category.$key.prize.§a$it §f값 미등록."); return false }
            prizes.add(prize)
        }
        ShopManager.categoryMap[key] = Category(key, itemKey, prizes)
        return true
    }

    private fun YamlConfiguration.loadShopData(key: String): Boolean {
        if (ShopManager.shopMap.contains(key)) { pluginMessage("shopData.${key} 값 중복."); return false }
        val size = getString("$key.size")?.toIntOrNull()?: run { pluginMessage("shopData.$key.§asize §f값 없음."); return false }
        val title = getString("$key.title")?: run { pluginMessage("shopData.$key.§atitle §f값 없음."); return false }
        val voidSlots = getIntegerList("$key.voidSlots").distinct().sorted().filter { it in 0 until size }
        val prizeSlots = getIntegerList("$key.prizeSlots").distinct().sorted().filter { it in 0 until size }
        val categorySlots = getIntegerList("$key.categorySlots").distinct().sorted().filter { it in 0 until size }
        val allSlots = voidSlots + prizeSlots + categorySlots
        if (allSlots.toSet().size != allSlots.size) { pluginMessage("shopData.$key.§a(voidSlots/prizeSlots/categorySlots) 값 중복"); return false }
        val categoryKeys = getStringList("$key.category")
        if (categoryKeys.isEmpty()) { pluginMessage("shopData.$key.§acategory §f값 없음"); return false }
        val categories = arrayListOf<Category>()
        categoryKeys.forEach {
            val category = ShopManager.categoryMap[it]?: run { pluginMessage("shopData.$key.category.§a$it §f값 미등록."); return false }
            categories.add(category)
        }
        if (categories.size > categorySlots.size) { pluginMessage("shopData.$key.§acategorySlots §f크기 오류.(category 개수 보다 커야함)"); return false }
        categories.forEach {
            if (it.prizes.size > prizeSlots.size) { pluginMessage("shopData.$key.§aprizeSlots §f크기 오류.(prize 개수 보다 커야함)"); return false }
        }
        ShopManager.shopMap[key] = ShopData(key, size, title, voidSlots, prizeSlots, categorySlots, categories)
        return true
    }

    private fun pluginMessage(message: String) { main.server.broadcastMessage("§e[${main.name}]§r $message") }

}