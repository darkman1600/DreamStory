package kr.dreamstory.community.listener

import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ChatListener: Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        main.schedule(SynchronizationContext.ASYNC) {
            CommunityManager.getCommunityData(event.player.uniqueId)!!.onDSChat(event)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerJoinEvent) {
        CommunityManager.unregister(event.player.uniqueId)
    }
}