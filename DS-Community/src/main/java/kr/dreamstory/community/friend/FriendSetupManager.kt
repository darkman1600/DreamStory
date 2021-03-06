package kr.dreamstory.community.friend

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.main
import kr.dreamstory.community.request.RequestManager
import kr.dreamstory.community.request.RequestType
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.utils.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object FriendSetupManager {
    fun setFriend(sender: CommandSender, first: String, second: String) {
        main.schedule(SynchronizationContext.ASYNC) {
            val p1 = Bukkit.getPlayerExact(first) ?: Bukkit.getOfflinePlayer(first)
            val p2 = Bukkit.getPlayerExact(second) ?: Bukkit.getOfflinePlayer(second)
            if(first.toUpperCase() != p1.name?.toUpperCase()) {
                sender.sendMessage("$first 님을 찾을 수 없습니다.")
                return@schedule
            }
            if(second.toUpperCase() != p2.name?.toUpperCase()) {
                sender.sendMessage("$second 님을 찾을 수 없습니다.")
                return@schedule
            }
            val p1UUID = p1.uniqueId
            val p2UUID = p2.uniqueId
            val p1cd = CommunityManager.getOfflineCommunityData(p1UUID) ?: run { MessageManager.pluginMessage(main,sender,"${p1.name} 님의 데이터를 찾을 수 없습니다."); return@schedule }
            val p2cd = CommunityManager.getOfflineCommunityData(p2UUID) ?: run { MessageManager.pluginMessage(main,sender,"${p2.name} 님의 데이터를 찾을 수 없습니다."); return@schedule }
            val p1Friends = p1cd.friends
            val p2Friends = p2cd.friends
            if(p1Friends.contains(p2UUID)) {
                sender.sendMessage("이미 서로 친구 관계입니다.")
                return@schedule
            }
            p1Friends.add(p2UUID)
            p2Friends.add(p1UUID)
            PlayerDataManger.getOfflinePlayerData(p1UUID)!!.set("friend_list", p1Friends.map { it.toString() }, !p1.isOnline)
            PlayerDataManger.getOfflinePlayerData(p2UUID)!!.set("friend_list", p2Friends.map { it.toString() }, !p2.isOnline)
            RequestManager.cancel(RequestType.ADD_FRIEND,p1UUID)
            RequestManager.cancel(RequestType.ADD_FRIEND,p2UUID)
            p1.player?.sendMessage("관리자에 의해 §e${p2.name} §f님이 친구 목록에 추가되었습니다.")
            p2.player?.sendMessage("관리자에 의해 §e${p1.name} §f님이 친구 목록에 추가되었습니다.")
        }
    }

    fun removeFriend(sender: CommandSender, first: String, second: String) {
        main.schedule(SynchronizationContext.ASYNC) {
            val p1 = Bukkit.getPlayerExact(first) ?: Bukkit.getOfflinePlayer(first)
            val p2 = Bukkit.getPlayerExact(second) ?: Bukkit.getOfflinePlayer(second)
            if (first.toUpperCase() != p1.name?.toUpperCase()) {
                sender.sendMessage("$first 님을 찾을 수 없습니다.")
                return@schedule
            }
            if (second.toUpperCase() != p2.name?.toUpperCase()) {
                sender.sendMessage("$second 님을 찾을 수 없습니다.")
                return@schedule
            }
            val p1UUID = p1.uniqueId
            val p2UUID = p2.uniqueId
            val p1cd = CommunityManager.getOfflineCommunityData(p1UUID) ?: run { MessageManager.pluginMessage(main,sender,"${p1.name} 님의 데이터를 찾을 수 없습니다."); return@schedule }
            val p2cd = CommunityManager.getOfflineCommunityData(p2UUID) ?: run { MessageManager.pluginMessage(main,sender,"${p2.name} 님의 데이터를 찾을 수 없습니다."); return@schedule }
            val p1Friends = p1cd.friends
            val p2Friends = p2cd.friends
            if (!p1Friends.contains(p2UUID)) {
                sender.sendMessage("이미 서로 친구 관계가 아닙니다.")
                return@schedule
            }
            p1Friends.remove(p2UUID)
            p2Friends.remove(p1UUID)
            PlayerDataManger.getOfflinePlayerData(p1UUID)!!.set("friend_list", p1Friends.map { it.toString() }, !p1.isOnline)
            PlayerDataManger.getOfflinePlayerData(p2UUID)!!.set("friend_list", p2Friends.map { it.toString() }, !p2.isOnline)
            p1.player?.sendMessage("관리자에 의해 §e${p2.name} §f님이 친구 목록에서 삭제되었습니다.")
            p2.player?.sendMessage("관리자에 의해 §e${p1.name} §f님이 친구 목록에서 삭제되었습니다.")
        }
    }

}