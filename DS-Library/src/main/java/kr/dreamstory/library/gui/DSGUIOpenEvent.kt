package com.dreamstory.library.gui

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class DSGUIOpenEvent(
    player: Player,
    val gui: DSGUI
): PlayerEvent(player), Cancellable {

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