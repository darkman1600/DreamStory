package kr.dreamstory.shop

import kr.dreamstory.library.gui.DSGUIManager
import kr.dreamstory.shop.shop.ShopGUI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPickupItemEvent

object Listener: Listener {
    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        DSGUIManager.getPlayerGUI(event.player.uniqueId).let {
            if (it is ShopGUI) event.isCancelled = true
        }
    }
}