package kr.dreamstory.community.request

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.request.events.RequestAcceptEvent
import kr.dreamstory.community.request.events.RequestDefuseEvent
import kr.dreamstory.community.request.events.RequestEvent
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.utils.SignMenuFactory
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.function.BiPredicate
import kotlin.collections.HashMap

object RequestManager {

    private val factoryMap = HashMap<UUID, RequestFactory>()
    private fun hasRequest(uuid: UUID) = factoryMap.containsKey(uuid)

    fun request(type: RequestType, req: Player, rec: String) {
        val receiver = Bukkit.getPlayerExact(rec)
        if(receiver == null) {
            req.sendMessage("대상을 찾을 수 없습니다.")
            return
        }
        request(type,req,receiver)
    }
    fun request(type: RequestType, req: Player, rec: Player) {
        val reqUUID = req.uniqueId
        val reqFactory = factoryMap[reqUUID]
        if(reqFactory != null) {
            val requester = reqFactory.requester
            val str = if(requester.uniqueId == reqUUID) {
                "§e${requester.name} §7님께 보낸 §a${type.display} §7요청"
            } else {
                "§e${requester.name} §7님께 받은 §a${type.display} §7요청"
            }
            val message = Component.text("이미 처리중인 요청이 있습니다.")
                .append(Component.newline())
                .append(Component.text(str))
            req.sendMessage(message)
            return
        }
        val recUUID = rec.uniqueId
        if(CommunityManager.getState(recUUID).checkIgnored(reqUUID)) {
            req.sendMessage(Component.text("요청이 차단되었습니다."))
            return
        }
        if(hasRequest(recUUID)) {
            val message = Component.text("해당 유저는 다른 요청을 처리하고 있습니다.")
            req.sendMessage(message)
            return
        }

        val newFactory = RequestFactory(type,req,rec)

        val event = RequestEvent(newFactory)
        event.callEvent()
        if(event.isCancelled) return

        factoryMap[reqUUID] = newFactory
        factoryMap[recUUID] = newFactory
    }


    fun accept(player: Player,type: RequestType) {
        val uuid = player.uniqueId
        val factory = factoryMap[uuid]
        val typeDisplay = type.display
        if(factory == null) {
            player.sendMessage(Component.text("받은 §a${typeDisplay} §f요청이 없습니다."))
            return
        }
        if(factory.receiver.uniqueId != uuid) {
            player.sendMessage(Component.text("받은 §a${typeDisplay} §f요청이 없습니다."))
            return
        }
        if(factory.type != type) {
            player.sendMessage(Component.text("받은 §a${typeDisplay} §f요청이 없습니다."))
            return
        }

        val event = RequestAcceptEvent(factory)
        event.callEvent()
        if(event.isCancelled) return

        factoryMap.remove(uuid)
        factoryMap.remove(factory.requester.uniqueId)

    }
    fun defuse(player: Player, type: RequestType) {
        val uuid = player.uniqueId
        val factory = factoryMap[uuid]
        val typeDisplay = type.display
        if(factory == null) {
            player.sendMessage(Component.text("받은 §a${typeDisplay} §f요청이 없습니다."))
            return
        }
        if(factory.receiver.uniqueId != uuid) {
            player.sendMessage(Component.text("받은 §a${typeDisplay} §f요청이 없습니다."))
            return
        }
        if(factory.type != type) {
            player.sendMessage(Component.text("받은 §a${typeDisplay} §f요청이 없습니다."))
            return
        }

        val event = RequestDefuseEvent(factory)
        event.callEvent()
        if(event.isCancelled) return

        factoryMap.remove(uuid)
        factoryMap.remove(factory.requester.uniqueId)
    }

    fun cancel(type: RequestType, uuid: UUID) {
        val factory = factoryMap[uuid] ?: return
        if(factory.type != type) return
        factoryMap.remove(factory.requester.uniqueId)
        factoryMap.remove(factory.receiver.uniqueId)
    }

}