package kr.dreamstory.library.item.dreamstory.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.item.dreamstory.item.DSDefaultItem
import com.dreamstory.library.item.dsitem.objs.DSItemData
import com.dreamstory.library.item.dsitem.objs.DSItemStack
import com.dreamstory.library.item.dsitem.objs.DSItemTier
import kr.dreamstory.library.main
import kr.dreamstory.library.message.MessageManager
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.io.File

object DSItemDataManager {

    var isLoaded = false

    private var item_instance = HashMap<String, DSItemStack>()
    private var data_instance = HashMap<String, DSItemData>()

    fun getDSItemKeyList() = item_instance.keys
    fun getDSItemStackList() = item_instance.values
    fun getDSItem(key: String) = item_instance[key]

    fun registerData(data: DSItemData) {
        data.createFile()
        data_instance[data.getFile().name] = data
    }
    fun registerAllData(vararg data: DSItemData) {
        data.forEach {
            it.createFile()
            data_instance[it.getFile().name] = it
        }
    }

    fun getNewDSItem(key: String,itemStack: ItemStack,canTrade: Boolean): DSItemStack = DSDefaultItem(key,itemStack,canTrade,DSItemTier.DEFAULT)

    internal fun loadAllData(sender: CommandSender) {
        item_instance = HashMap()
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            var warnCount = 0
            val fileSize = (File("${main.dataFolder.path}\\database").listFiles() ?: Array(0) {}).size
            while(data_instance.size != fileSize) {
                if(warnCount >= 600) {
                    MessageManager.pluginMessage(main,sender,"아이템 로드가 지연되고있습니다.\ndatabase 파일 안에 데이터와 관련 없는 파일이 있다면,\n 제거 후 서버를 다시 켜주세요.")
                    warnCount = 0
                }
                waitFor(1)
                warnCount ++
            }

            val list = ArrayList<DSItemStack>()
            data_instance.values.forEach {
                list.addAll(it.getDSItems())
            }

            repeat(list.size) {
                if(list.isEmpty()) return@repeat
                val item = list[0]
                item_instance[item.key] = item
                list.remove(item)
                if(list.any { it.key == item.key }) {
                    val overlapList = list.filter { item.key == it.key }
                    val text = overlapList.joinToString { overlapItem -> "§7[ §7key : §a${overlapItem.key} §7/ type : §a${overlapItem.type} §7]\n" }.replace(", ","")
                    MessageManager.pluginMessage(main,sender,"아이템 키 중복!\n§7[ §7key : §a${item.key} §7/ type : §a${item.type} §7]\n$text")
                    overlapList.forEach { list.remove(it) }
                }
            }
            MessageManager.pluginMessage(main,sender,"§a${item_instance.size} §f개의 아이템 로드 완료.")
            isLoaded = true
        }
    }

}