package kr.dreamstory.library.listener

import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.extension.database
import kr.dreamstory.library.main
import kr.dreamstory.library.message.MessageManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import java.util.*
import kotlin.collections.HashSet

class DetectListener {

    private val waitingSet = HashSet<UUID>()

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

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(e: PlayerJoinEvent) {
        val uuid = e.player.uniqueId
        waitingSet.add(uuid)
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            var count = 0
            while (count ++ <= 200) {
                if(PlayerDataManger.getPlayerData(uuid) != null) break
                waitFor(1)
            }
            if(PlayerDataManger.getPlayerData(uuid) == null) {
                val player = e.player
                MessageManager.pluginMessage(main,"[ §e${player.name} §f] 님의 데이터를 로드하는데 실패하였습니다.")
                player.kickPlayer("데이터를 불러오는데 실패하였습니다.\n같은 현상이 반복될 경우,\n관리자에게 문의하세요.")
            }
            waitingSet.remove(uuid)
        }
    }
}