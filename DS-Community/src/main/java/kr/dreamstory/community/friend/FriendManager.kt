package kr.dreamstory.community.friend

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.main
import kr.dreamstory.community.request.RequestFactory
import kr.dreamstory.community.request.RequestType
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object FriendManager {

    fun onAccept(factory: RequestFactory): Boolean {
        val req = factory.requester
        val rec = factory.receiver
        if(factory.type != RequestType.ADD_FRIEND) {
            rec.sendMessage("§a${factory.type.display} §f요청 먼저 처리하세요.")
            return false
        }
        val reqUUID = req.uniqueId
        val recUUID = rec.uniqueId
        CommunityManager.getState(reqUUID).friends.add(recUUID)
        CommunityManager.getState(recUUID).friends.add(reqUUID)
        PlayerDataManger.getPlayerData(reqUUID).addToStringList(main,"friends",recUUID.toString())
        PlayerDataManger.getPlayerData(recUUID).addToStringList(main,"friends",reqUUID.toString())
        req.sendMessage("§e${rec.name} §f님과 친구가 되었습니다.")
        rec.sendMessage("§e${req.name} §f님과 친구가 되었습니다.")
        return true
    }

    fun onRequest(factory: RequestFactory): Boolean {
        val req = factory.requester
        val rec = factory.receiver
        if(factory.type != RequestType.ADD_FRIEND) {
            rec.sendMessage("§a${factory.type.display} §f요청 먼저 처리하세요.")
            return false
        }
        if(CommunityManager.getState(req.uniqueId).friends.contains(rec.uniqueId)) {
            req.sendMessage("이미 해당 유저와 친구입니다.")
            return false
        }
        val acceptText = Component.text("§a[ 수락 ]")
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/친구 수락"))
        val refuseText = Component.text("§c[ 거절 ]")
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 거절"))
        val ignoreText = Component.text("§4[ 차단 ]")
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/차단 추가 ${req.name}"))
        val message = Component.text("§e${req.name} §f님으로 부터 친구 요청이 도착했습니다.")
            .append(Component.newline())
            .append(Component.text("버튼을 클릭하세요!"))
            .append(acceptText)
            .append(Component.text(" "))
            .append(refuseText)
            .append(Component.text(" "))
            .append(ignoreText)
        rec.sendMessage(message)
        req.sendMessage("친구를 신청하였습니다.")
        return true
    }

    fun onDefuse(factory: RequestFactory): Boolean {
        factory.requester.sendMessage("친구 요청이 거절되었습니다.")
        factory.receiver.sendMessage("요청이 거절되었습니다.")
        return true
    }

    fun remove(player: Player, friendName: String) {
        main.schedule(SynchronizationContext.ASYNC) {
            val uuid = player.uniqueId
            val friend = Bukkit.getPlayerExact(friendName) ?: Bukkit.getOfflinePlayer(friendName)
            if(friendName.toUpperCase() != friend.name?.toUpperCase()) {
                player.sendMessage("$friendName 님을 찾을 수 없습니다.")
                return@schedule
            }
            val friendUUID = friend.uniqueId
            val pFriends = CommunityManager.getState(uuid).friends
            val fFriends = CommunityManager.getState(friendUUID,!friend.isOnline).friends
            if(!pFriends.contains(friendUUID)) {
                player.sendMessage("친구 목록에서 찾을 수 없습니다.")
                return@schedule
            }
            pFriends.remove(friendUUID)
            fFriends.remove(uuid)
            PlayerDataManger.getPlayerData(uuid).set(main,"friends",pFriends.map { it.toString() })
            PlayerDataManger.getPlayerData(friendUUID,!friend.isOnline).set(main,"friends",fFriends.map { it.toString() },!friend.isOnline)
            player.sendMessage("${friend.name} 님이 친구 목록에서 삭제되었습니다.")
            friend.player?.sendMessage("${player.name} 님이 친구 목록에서 삭제되었습니다.")
        }
    }

}