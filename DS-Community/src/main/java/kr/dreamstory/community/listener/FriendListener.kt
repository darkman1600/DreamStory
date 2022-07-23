package kr.dreamstory.community.listener

import kr.dreamstory.community.friend.FriendManager
import kr.dreamstory.community.main
import kr.dreamstory.community.request.events.RequestAcceptEvent
import kr.dreamstory.community.request.events.RequestDefuseEvent
import kr.dreamstory.community.request.events.RequestEvent
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FriendListener: Listener {
    @EventHandler
    fun onFriendRequest(event: RequestEvent) {
        if(!FriendManager.onRequest(event.factory)) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onFriendAccept(event: RequestAcceptEvent) {
        main.schedule(SynchronizationContext.ASYNC) {
            if(!FriendManager.onAccept(event.factory)) {
                event.isCancelled = true
                return@schedule
            }
        }
    }

    @EventHandler
    fun onFriendDefuse(event: RequestDefuseEvent) {
        main.schedule(SynchronizationContext.ASYNC) {
            FriendManager.onDefuse(event.factory)
        }
    }


}