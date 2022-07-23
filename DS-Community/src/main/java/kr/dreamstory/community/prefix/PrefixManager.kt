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

    fun addPrefix(uuid: UUID, key: String) {
        val prefix = getPrefix(key) ?: return
        val d = PlayerDataManger.getPlayerData(uuid)!!
        val cd = CommunityManager.getCommunityData(uuid)!!
        cd.prefixList.add(prefix)
        d.set("prefix_list",cd.prefixList)
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