package kr.dreamstory.data_loader.listener

import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.data_loader.main
import kr.dreamstory.data_loader.objs.DataContainer
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.utils.message.MessageManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import java.util.*
import kotlin.collections.HashSet

class LoadingDetectListener: Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: PlayerMoveEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: InventoryOpenEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: PlayerDropItemEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: PlayerAttemptPickupItemEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: AsyncChatEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: PlayerInteractEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: PlayerInteractAtEntityEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCheck(e: EntityDamageEvent) { if(waitingSet.contains(e.entity.uniqueId)) e.isCancelled = true }

    private val waitingSet = HashSet<UUID>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(e: PlayerJoinEvent) {
        val uuid = e.player.uniqueId
        waitingSet.add(uuid)
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            val dc = DataContainer(e.player)
            var count = 0
            while (count ++ <= 200) {
                if(dc.isLoaded) break
                waitFor(1)
            }
            waitingSet.remove(uuid)
            if(!dc.isLoaded) {
                switchContext(SynchronizationContext.SYNC)
                val player = e.player
                MessageManager.pluginMessage(main,"[ §e${player.name} §f] 님의 데이터를 로드하는데 실패하였습니다.")
                player.kickPlayer("데이터를 불러오는데 실패하였습니다.\n같은 현상이 반복될 경우,\n관리자에게 문의하세요.")
            }
        }
    }
}