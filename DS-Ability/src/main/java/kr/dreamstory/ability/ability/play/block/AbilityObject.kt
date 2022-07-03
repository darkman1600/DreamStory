package kr.dreamstory.ability.ability.play.block

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.AbilityType
import com.dreamstory.ability.extension.isDropItem
import com.dreamstory.ability.manager.ItemListManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack

abstract class AbilityObject(
    val type: AbilityType,
    val key: String,
    val regionId: Int,
    var prevLevel: Int,
    var maxLevel: Double,
    var exp: Long,
    var drop: ItemStack,
    data: String? = null
) {

    abstract fun load()

    val server by lazy { main.server }
    val pluginManager by lazy { main.server.pluginManager }
    val dropItem: ItemStack get() = drop.clone()

    fun setDropItem(item: ItemStack): Boolean {
        if(item.isDropItem) drop = item
        else return false
        return true
    }

    fun setConfig(config: FileConfiguration) {
        config.set("$key.minLevel", prevLevel)
        config.set("$key.maxLevel", maxLevel)
        config.set("$key.drop", ItemListManager.itemStackArrayToBase64(drop))
        config.set("$key.exp", exp)
        config.set("$key.region", regionId)
        config.set("$key.type", type.name)
        config.set("$key.data", toDataString())
    }

    abstract fun toDataString(): String

}