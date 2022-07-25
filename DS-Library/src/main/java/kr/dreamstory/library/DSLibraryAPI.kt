package kr.dreamstory.library

import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.utils.message.MessageManager
import kr.dreamstory.library.utils.SignMenuFactory
import org.bukkit.entity.Player
import java.util.UUID

object DSLibraryAPI {
    val messageManager = MessageManager
    val dsItemManager = DSItemManager
    val playerDataManager = PlayerDataManger

    val signMenuFactory by lazy { SignMenuFactory(main) }

    val dsOnlinePlayers = HashSet<Player>()
    fun getPlayerExact(name: String): Player? = dsOnlinePlayers.firstOrNull { name.equals(it.name, ignoreCase = true) }
    fun getPlayer(uuid: UUID): Player? = dsOnlinePlayers.firstOrNull { uuid == it.uniqueId }

}