package kr.dreamstory.community.prefix

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.message.MessageManager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

object PrefixManager {

    private val prefixMap = HashMap<String,Prefix>()
    fun getPrefix(display: String) = prefixMap[display]

    fun addPrefix(uuid: UUID, key: String, offline: Boolean = false) {
        val prefix = getPrefix(key) ?: return
        val d = PlayerDataManger.getPlayerData(uuid)
        if(offline) {
            d.addToStringList(main,"prefix_list",key,true)
        } else {
            val cd = CommunityManager.getState(uuid)
            cd.prefixList.add(prefix)
            d.addToStringList(main,"prefix_list",key)
        }
    }

    fun loadPrefix() {
        main.schedule(SynchronizationContext.ASYNC) {
            val file = File("${main.dataFolder.path}\\database","prefix.yml")
            val config = YamlConfiguration.loadConfiguration(file)
            config.save(file)
            config.getKeys(false).forEach loop@ {
                val display = config.getString("$it.display") ?: run { MessageManager.pluginMessage(main,"prefix $it 의 display 값 없음."); return@loop }
                val prefix = Prefix(display)
                prefixMap[it] = prefix
            }
        }
    }

}