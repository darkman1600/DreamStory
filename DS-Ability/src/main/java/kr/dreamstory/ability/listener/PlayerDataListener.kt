package kr.dreamstory.ability.listener

import kr.dreamstory.ability.ability.main
import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.ability.manager.AbilityManager
import kr.dreamstory.ability.manager.ActionBarManager
import kr.dreamstory.ability.manager.LogManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.DataUpdateEvent
import kr.dreamstory.library.data.PlayerDataLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import java.util.*
import kotlin.collections.HashSet

class PlayerDataListener: Listener {

    val server = main.server

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player
        AbilityManager.saveAndQuit(p)
        LogManager.saveLog(p, LogManager.LogType.QUIT)
    }

    @EventHandler
    fun onUpdate(e: DataUpdateEvent) {
        AbilityManager.updateAll()
    }

    @EventHandler
    fun onDataLoad(event: PlayerDataLoadEvent) {
        if(event.playerData.getBoolean("option.ability_actionbar_toggle",true)) {
            ActionBarManager.actionBarSet.add(event.player)
        }
    }

}