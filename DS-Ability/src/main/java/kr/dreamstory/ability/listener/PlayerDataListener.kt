package com.dreamstory.ability.listener

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.manager.*
import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.ability.manager.AbilityManager
import kr.dreamstory.ability.manager.LogManager
import kr.dreamstory.library.data.DataSaveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import java.util.*

class PlayerDataListener: Listener {

    val server = main.server
    private val scheduler by lazy { server.scheduler }
    private val waitingSet = HashSet<UUID>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerMoveEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: InventoryOpenEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerDropItemEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerAttemptPickupItemEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: AsyncChatEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerInteractEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerInteractAtEntityEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: EntityDamageEvent) { if(waitingSet.contains(e.entity.uniqueId)) e.isCancelled = true }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player
        AbilityManager.saveAndQuit(p.uniqueId)
        LogManager.saveLog(p, LogManager.LogType.QUIT)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        AbilityManager.getNewAbility(e.player.uniqueId)
    }

    @EventHandler
    fun onSave(e: DataSaveEvent) {
        AbilityManager.saveAll()
    }

}