package kr.dreamstory.shop

import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.prize.Price
import kr.dreamstory.shop.prize.PriceType
import kr.dreamstory.shop.prize.Prize
import kr.dreamstory.shop.shop.ShopData
import kr.dreamstory.shop.shop.ShopIcon
import kr.dreamstory.shop.shop.ShopManager
import org.apache.commons.io.FileUtils
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.Yaml
import java.io.File

object DataLoader {

    fun load() {
        ShopManager.clearShop()
        val directory = File("${main.dataFolder.path}\\database")
        if(!directory.exists()) {
            try {
                directory.mkdirs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }
        val files = File("${main.dataFolder.path}\\database").listFiles() ?: return
        files.forEach { file ->
            if (!file.isFile) return@forEach
            val config = YamlConfiguration.loadConfiguration(file)
            getShopList(config).forEach {
                ShopManager.inputShop(it.key,it )// 여기까지 함 !
            }
        }
    }

    private fun getShopList(config: YamlConfiguration): List<ShopData> {
        val shopList = arrayListOf<ShopData>()
        config.getKeys(false).forEach loop@ { key ->
            val size = config.getString("$key.size")?.toIntOrNull() ?: 27
            val title = config.getString("$key.title") ?: "null"
            shopList.add(ShopData(key ,size, title, getCategories(config, "$key.category")))
        }
        return shopList
    }

    private fun getCategories(config: YamlConfiguration, key: String): List<Category> {
        val categories = arrayListOf<Category>()
        config.getStringList(key).forEach { category ->
            val slot = config.getString("$key.$category.slot")?.toIntOrNull() ?: 26
            val icon = config.getString("$key.$category.icon") ?: "null"
            categories.add(Category(category, ShopIcon(icon,slot), getPrizes(config, "$key.category.$category.item")))
        }
        return categories
    }

    private fun getPrizes(config: YamlConfiguration, key: String): List<Prize> {
        val prizes = arrayListOf<Prize>()
        config.getStringList(key).forEach { item ->
            val slot = config.getString("$key.$item.slot")?.toIntOrNull() ?: 26
            val price = config.getString("$key.$item.price")?.toIntOrNull() ?: 999
            prizes.add(Prize(ShopIcon(item,slot), Price(price,PriceType.MONEY)))
        }
        return prizes
    }

}