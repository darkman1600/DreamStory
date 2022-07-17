package kr.dreamstory.community.prefix.events

import kr.dreamstory.community.prefix.Prefix
import kr.dreamstory.community.request.RequestFactory
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PrefixAddEvent(
    val prefix: Prefix
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