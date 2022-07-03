package kr.dreamstory.library.data

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DataSaveEvent: Event(true) {

    override fun getHandlers(): HandlerList { return HANDLERLIST }

    companion object {
        var HANDLERLIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERLIST
        }
    }
}