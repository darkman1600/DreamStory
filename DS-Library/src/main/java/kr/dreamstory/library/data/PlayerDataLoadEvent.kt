package kr.dreamstory.library.data

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerDataLoadEvent(val player: Player, val playerData: PlayerData): Event(true), Cancellable {

    override fun getHandlers(): HandlerList { return HANDLERLIST }

    companion object {
        var HANDLERLIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERLIST
        }
    }

    private var isCancelled: Boolean = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }
    override fun setCancelled(boolean: Boolean) {
        isCancelled = boolean
    }

}