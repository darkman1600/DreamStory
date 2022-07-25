package kr.dreamstory.player_options.listener

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataLoadEvent
import kr.dreamstory.player_options.main
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class UtilOptionListener: Listener {

    companion object {
        val pickupDetectSet = HashSet<UUID>()
        val dropDetectSet = HashSet<UUID>()
    }

    @EventHandler
    fun onDataLoad(event: PlayerDataLoadEvent) {
        val d = event.playerData
        val uuid = d.uuid
        if(!d.getBoolean("option.pickup",true)) pickupDetectSet.add(uuid)
        if(!d.getBoolean("option.drop",true)) dropDetectSet.add(uuid)
    }

    @EventHandler
    fun onPickup(event: PlayerAttemptPickupItemEvent) {
        if(pickupDetectSet.contains(event.player.uniqueId)) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if(dropDetectSet.contains(event.player.uniqueId)) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        val player = event.player
        main.schedule(SynchronizationContext.ASYNC) {
            for(p in DSLibraryAPI.dsOnlinePlayers) {
                val cd = CommunityManager.getCommunityData(p.uniqueId) ?: continue
                if(cd.friends.contains(player.uniqueId)) {
                    p.sendMessage("§e친구 §f${player.name} 님이 접속하였습니다.")
                }
            }
        }
    }
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(null)
        val player = event.player
        main.schedule(SynchronizationContext.ASYNC) {
            for(p in Bukkit.getOnlinePlayers()) {
                val cd = CommunityManager.getCommunityData(p.uniqueId) ?: continue
                if(cd.friends.contains(player.uniqueId)) {
                    p.sendMessage("§e친구 §f${player.name} 님이 접속을 종료하였습니다.")
                }
            }
        }
    }

}