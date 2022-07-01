package kr.dreamstory.library.item.dreamstory.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.item.dreamstory.item.*
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemTier
import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType
import kr.dreamstory.library.item.dreamstory.item.objs.DSArmor
import kr.dreamstory.library.item.dreamstory.item.objs.DSDefaultItem
import kr.dreamstory.library.item.dreamstory.item.objs.DSTool
import kr.dreamstory.library.item.dreamstory.item.objs.DSUpgradeStone
import kr.dreamstory.library.item.minecraft.color
import kr.dreamstory.library.item.minecraft.setStringNbt
import kr.dreamstory.library.item.minecraft.translateHexColorCodes
import kr.dreamstory.library.main
import kr.dreamstory.library.message.MessageManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

object DSItemManager {

    var isLoaded = false
        private set

    private var item_instance = HashMap<String, DSItemStack>()

    fun getDSItemKeyList() = item_instance.keys
    fun getDSItemStackList() = item_instance.values
    fun getDSItem(key: String) = item_instance[key]

    fun getNewDSItem(key: String,itemStack: ItemStack,canTrade: Boolean): DSItemStack = DSDefaultItem(key,itemStack,canTrade,
        DSItemTier.DEFAULT)


    fun load(sender: CommandSender) {
        main.server.scheduler.schedule(main,SynchronizationContext.ASYNC) {
            item_instance = HashMap()
            val directory = File("${main.dataFolder.path}\\database\\ds_items")
            if(!directory.exists()) {
                try {
                    directory.mkdirs()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@schedule
            }
            val files = directory.listFiles() ?: run { MessageManager.pluginMessage(main,sender,"ds_item 관련 데이터 파일을 찾을 수 없습니다."); return@schedule }
            val itemList = ArrayList<DSItemStack>()
            var count = 0
            files.forEach { file ->
                val config = YamlConfiguration.loadConfiguration(file)
                val keyList = config.getKeys(false)
                config.run {
                    keyList.forEach loop@ { key ->
                        val materialTag = getString("$key.material") ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §amaterial §f값 없음."); return@loop }
                        val material = Material.getMaterial(materialTag.toUpperCase()) ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §amaterial §f값 오류"); return@loop }
                        val typeTag = getString("$key.type") ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §atype §f값 없음."); return@loop }
                        val type = DSItemType.getDSItemType(typeTag.toUpperCase()) ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §atype §f값 오류"); return@loop }
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
                        val dsItem: DSItemStack
                        when(type) {
                            DSItemType.ARMOR -> {
                                if(name == null) { MessageManager.pluginMessage(main,"$key 아이템의 §a이름 §f값 없음."); return@loop }
                                dsItem = DSArmor(key,item,trade,tier,name,lore)
                            }
                            DSItemType.TOOL -> {
                                if(name == null) { MessageManager.pluginMessage(main,"$key 아이템의 §a이름 §f값 없음."); return@loop }
                                dsItem = DSTool(key,item,trade,tier,name,lore)
                            }
                            DSItemType.DEFAULT -> {
                                dsItem = DSDefaultItem(key,item,trade,tier)
                            }
                            DSItemType.UPGRADE_STONE -> {
                                val upgradeTypeTag = getString("$key.upgrade_type") ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §aupgrade_type §f값 없음."); return@loop }
                                val upgradeType = DSItemType.getDSItemType(upgradeTypeTag) ?: run { MessageManager.pluginMessage(main,"$key 아이템의 §aupgrade_type §f값 오류."); return@loop }
                                dsItem = DSUpgradeStone(key,item,trade,tier,upgradeType)
                            }
                        }
                        itemList.add(dsItem)
                        count ++
                    }
                }
            }
            repeat(itemList.size) {
                if(itemList.isEmpty()) return@repeat
                val item = itemList[0]
                item_instance[item.key] = item
                itemList.remove(item)
                if(itemList.any { it.key == item.key }) {
                    val overlapList = itemList.filter { item.key == it.key }
                    val text = overlapList.joinToString { overlapItem -> "§7[ §7key : §a${overlapItem.key} §7/ type : §a${overlapItem.type} §7]\n" }.replace(", ","")
                    MessageManager.pluginMessage(main,sender,"아이템 키 중복!\n§7[ §7key : §a${item.key} §7/ type : §a${item.type} §7]\n$text")
                    overlapList.forEach { itemList.remove(it) }
                }
            }
            MessageManager.pluginMessage(main,sender,"§a${item_instance.size} §f개의 아이템 로드 완료.")
            isLoaded = true
        }
    }
}