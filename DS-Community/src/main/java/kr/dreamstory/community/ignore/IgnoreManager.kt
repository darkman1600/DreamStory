package kr.dreamstory.community.ignore

import io.sentry.protocol.Message
import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.library.message.MessageType
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object IgnoreManager {
    fun addIgnore(player: Player, targetName: String) {
        main.schedule(SynchronizationContext.ASYNC) {
            val target = Bukkit.getPlayerExact(targetName) ?: Bukkit.getOfflinePlayer(targetName)
            if(targetName.toUpperCase() != target.name?.toUpperCase()) {
                player.sendMessage("$targetName 님을 찾을 수 없습니다.")
                return@schedule
            }
            val targetUUID = target.uniqueId
            val uuid = player.uniqueId
            val pIgnoreList = CommunityManager.getCommunityData(uuid)!!.ignoreList
            if(pIgnoreList.contains(targetUUID)) {
                player.sendMessage("이미 차단 되어있습니다.")
                return@schedule
            }
            val tData = PlayerDataManger.getOfflinePlayerData(targetUUID) ?: run { MessageManager.systemMessage(player, MessageType.WARNING,"서버에 등록되지 않은 플레이어입니다."); return@schedule }
            val tIgnoreMeList = CommunityManager.getOfflineCommunityData(targetUUID)!!.ignoreMeList
            pIgnoreList.add(targetUUID)
            tIgnoreMeList.add(uuid)
            PlayerDataManger.getPlayerData(uuid)!!.set("ignore_list",pIgnoreList.map { it.toString() })
            tData.set("ignore_me_list",tIgnoreMeList.map { it.toString() },!target.isOnline)
            player.sendMessage("이제 ${target.name} 님을 무시합니다.")
        }
    }
    fun removeIgnore(player: Player, targetName: String) {
        main.schedule(SynchronizationContext.ASYNC) {
            val target = Bukkit.getPlayerExact(targetName) ?: Bukkit.getOfflinePlayer(targetName)
            if(targetName.toUpperCase() != target.name?.toUpperCase()) {
                player.sendMessage("$targetName 님을 찾을 수 없습니다.")
                return@schedule
            }
            val targetUUID = target.uniqueId
            val uuid = player.uniqueId
            val pIgnoreList = CommunityManager.getCommunityData(uuid)!!.ignoreList
            if(!pIgnoreList.contains(targetUUID)) {
                player.sendMessage("차단 된 플레이어가 아닙니다.")
                return@schedule
            }
            val tIgnoreMeList = CommunityManager.getOfflineCommunityData(targetUUID)!!.ignoreMeList
            pIgnoreList.remove(targetUUID)
            tIgnoreMeList.remove(uuid)
            PlayerDataManger.getPlayerData(uuid)!!.set("ignore_list",pIgnoreList.map { it.toString() })
            PlayerDataManger.getOfflinePlayerData(targetUUID)!!.set("ignore_me_list",tIgnoreMeList.map { it.toString() },!target.isOnline)
            player.sendMessage("이제 ${target.name} 님을 무시하지 않습니다.")
        }
    }
}