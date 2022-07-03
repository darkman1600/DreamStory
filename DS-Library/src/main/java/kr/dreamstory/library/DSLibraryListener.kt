package kr.dreamstory.library

import kr.dreamstory.library.gui.DSGUIManager
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.data.DataSaveEvent
import kr.dreamstory.library.economy.EconomyManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class DSLibraryListener: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val uuid = event.player.uniqueId
        PlayerDataManger.getNewPlayerData(uuid)
        EconomyManager.inputData(uuid)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId
        EconomyManager.saveAndQuit(uuid)
        PlayerDataManger.getPlayerData(uuid).save()
        PlayerDataManger.unregister(uuid)
    }

    @EventHandler
    fun onSave(event: DataSaveEvent) {
        EconomyManager.saveAll()
    }

    @EventHandler
    fun onGuiClick(event: InventoryClickEvent) { DSGUIManager.getPlayerGUI(event.whoClicked.uniqueId)?.guiClick(event) }
    @EventHandler
    fun onGuiClose(event: InventoryCloseEvent) { DSGUIManager.getPlayerGUI(event.player.uniqueId)?.guiClose(event) }
    @EventHandler
    fun onGuiDrag(event: InventoryDragEvent) { DSGUIManager.getPlayerGUI(event.whoClicked.uniqueId)?.guiDrag(event) }

}