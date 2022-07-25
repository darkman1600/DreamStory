package kr.dreamstory.library.listener

import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.gui.DSGUIManager
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.data.DataUpdateEvent
import kr.dreamstory.library.data.PlayerDataLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LibraryListener: Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        PlayerDataManger.saveAndQuit(event.player)
    }

    @EventHandler
    fun onSave(event: DataUpdateEvent) {
        PlayerDataManger.updateAll()
    }

    @EventHandler
    fun onDataLoad(event: PlayerDataLoadEvent) {
        DSLibraryAPI.dsOnlinePlayers.add(event.player)
    }

    @EventHandler
    fun onGuiClick(event: InventoryClickEvent) { DSGUIManager.getPlayerGUI(event.whoClicked.uniqueId)?.guiClick(event) }
    @EventHandler
    fun onGuiClose(event: InventoryCloseEvent) { DSGUIManager.getPlayerGUI(event.player.uniqueId)?.guiClose(event) }
    @EventHandler
    fun onGuiDrag(event: InventoryDragEvent) { DSGUIManager.getPlayerGUI(event.whoClicked.uniqueId)?.guiDrag(event) }

}