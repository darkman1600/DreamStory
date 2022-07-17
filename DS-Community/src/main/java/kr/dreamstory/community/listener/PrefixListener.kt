package kr.dreamstory.community.listener

import kr.dreamstory.community.prefix.events.PrefixAddEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PrefixListener: Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if(event.hasItem()) {
            event.player.inventory.setItemInMainHand(null)
        }
    }
}