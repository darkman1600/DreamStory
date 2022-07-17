package kr.dreamstory.community.request.events

import kr.dreamstory.community.request.RequestFactory
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class RequestEvent(
    val factory: RequestFactory
): Event(), Cancellable {

    override fun getHandlers(): HandlerList { return HANDLERLIST }

    companion object {
        var HANDLERLIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERLIST
        }
    }

    private var isCancelled = false
    override fun isCancelled(): Boolean { return isCancelled }
    override fun setCancelled(boolean: Boolean) { isCancelled = boolean }
}