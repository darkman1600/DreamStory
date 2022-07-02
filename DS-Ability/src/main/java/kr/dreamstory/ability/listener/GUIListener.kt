package com.dreamstory.ability.listener

import kr.dreamstory.ability.ability.guiMap
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class GUIListener: Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun guiClick(e: InventoryClickEvent) { guiMap[e.whoClicked.uniqueId]?.guiClick(e) }

    @EventHandler(priority = EventPriority.HIGH)
    fun guiClose(e: InventoryCloseEvent) { guiMap[e.player.uniqueId]?.guiClose(e) }

    @EventHandler(priority = EventPriority.HIGH)
    fun guiDrag(e: InventoryDragEvent) { guiMap[e.whoClicked.uniqueId]?.guiDrag(e) }

}