package kr.dreamstory.data_loader.objs

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.dreamstory.ability.manager.AbilityManager
import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.data_loader.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataLoadEvent
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.utils.message.MessageManager
import org.bukkit.entity.Player

class DataContainer(player: Player) {
    var isLoaded: Boolean = false
        private set

    init {
        load(player)
    }

    fun load(player: Player) {
        main.schedule(SynchronizationContext.ASYNC) {
            fun message() { MessageManager.pluginMessage(main,"${player.name} 님의 데이터를 불러오는데 실패하였습니다.") }
            if(!PlayerDataManger.loadPlayerData(player)) { message(); return@schedule }
            val playerData = PlayerDataManger.getPlayerData(player.uniqueId)!!
            if(!AbilityManager.loadAbility(playerData)) { message(); return@schedule }
            if(!CommunityManager.loadCommunityData(playerData)) { message(); return@schedule }

            val event = PlayerDataLoadEvent(player,playerData)
            event.callEvent()
            if(event.isCancelled) return@schedule

            isLoaded = true
        }
    }

}