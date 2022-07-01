package kr.dreamstory.library.item.dreamstory.item

import com.dreamstory.library.item.dsitem.objs.DSItemData
import com.dreamstory.library.item.dsitem.objs.DSItemTier
import kr.dreamstory.library.item.minecraft.color
import kr.dreamstory.library.item.minecraft.setStringNbt
import kr.dreamstory.library.item.minecraft.translateHexColorCodes
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import org.apache.commons.io.FileUtils
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

class DSDefaultItemData: DSItemData() {

    var load = false
        private set

    override fun isLoaded(): Boolean = load
    override fun getFile(): File = File("${main.dataFolder.path}\\database","DSItem-default.yml")
    override fun createFile() {
        if(!getFile().exists()) {
            try {
                FileUtils.copyToFile(main.getResource("DSItem-default.yml"), File("${DSLibraryAPI.getDataFolder().path}\\database","DSItem-default.yml"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun getConfig(): YamlConfiguration = YamlConfiguration.loadConfiguration(getFile())
    override fun getDSItems(): List<DSDefaultItem> {
        val list = ArrayList<DSDefaultItem>()
        if(!getFile().exists()) {
            try {
                FileUtils.copyToFile(main.getResource("DSItem-default.yml"), File("${main.dataFolder.path}\\database","DSItem-default.yml"))
                getDSItems()
            } catch (e: Exception) {
                e.printStackTrace()
                return list
            }
        } else {
            main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
                val conf = getConfig()
                val keyList = conf.getKeys(false)
                var count = 0
                conf.run {
                    keyList.forEach loop@ { key ->
                        val materialTag = getString("$key.material") ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §amaterial §f값 없음."); return@loop }
                        val material = Material.getMaterial(materialTag.toUpperCase()) ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §amaterial §f값 오류"); return@loop }
                        val tierTag = getString("$key.tier") ?: "default"
                        val tier = DSItemTier.getDSItemTier(tierTag.toUpperCase()) ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §atier §f값 오류"); return@loop }
                        val item = ItemStack(material)
                        val customModel = getString("$key.model")?.toIntOrNull()
                        val name = getString("$key.name")?.translateHexColorCodes()
                        val lore = getStringList("$key.lore").map { it.color() }
                        val meta = item.itemMeta
                        meta.apply {
                            setDisplayName(name)
                            setLore(lore)
                            setCustomModelData(customModel)
                            setStringNbt("DSItemKey",key)
                        }
                        val trade = (getString("$key.trade") ?: "true").toBooleanStrictOrNull() ?: true
                        item.itemMeta = meta
                        list.add(DSDefaultItem(key,item,trade,tier))
                        count ++
                    }
                }
                load = true
            }
        }
        return list
    }


}