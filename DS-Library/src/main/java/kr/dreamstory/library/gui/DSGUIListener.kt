package com.dreamstory.library.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class DSGUIListener: Listener {

    @EventHandler
    fun onGuiClick(event: InventoryClickEvent) { DSGUIManager.getPlayerGUI(event.whoClicked.uniqueId)?.guiClick(event) }
    @EventHandler
    fun onGuiClose(event: InventoryCloseEvent) { DSGUIManager.getPlayerGUI(event.player.uniqueId)?.guiClose(event) }
    @EventHandler
    fun onGuiDrag(event: InventoryDragEvent) { DSGUIManager.getPlayerGUI(event.whoClicked.uniqueId)?.guiDrag(event) }

}