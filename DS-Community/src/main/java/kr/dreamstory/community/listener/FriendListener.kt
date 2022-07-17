package kr.dreamstory.community.listener

import kr.dreamstory.community.friend.FriendManager
import kr.dreamstory.community.request.events.RequestAcceptEvent
import kr.dreamstory.community.request.events.RequestDefuseEvent
import kr.dreamstory.community.request.events.RequestEvent
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
        if(!FriendManager.onAccept(event.factory)) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onFriendDefuse(event: RequestDefuseEvent) {
        FriendManager.onDefuse(event.factory)
    }


}