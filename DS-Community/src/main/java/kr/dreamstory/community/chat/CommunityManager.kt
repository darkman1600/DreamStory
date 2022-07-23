package kr.dreamstory.community.chat

import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.data.PlayerDataManger
import java.util.UUID

object CommunityManager {
    private val dataMap = HashMap<UUID, CommunityData>()

    fun getCommunityData(uuid: UUID): CommunityData? = dataMap[uuid]

    @Deprecated("동기 스레드에서 사용하지 마세요.", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith(""))
    fun getOfflineCommunityData(uuid: UUID): CommunityData? {
        val cd = dataMap[uuid]
        return if(cd == null) {
            val new = CommunityData(PlayerDataManger.getOfflinePlayerData(uuid) ?: return null)
            dataMap[uuid] = new
            dataMap[uuid]
        } else {
            cd
        }
    }
    fun loadCommunityData(playerData: PlayerData): Boolean {
        val newData = CommunityData(playerData)
        dataMap[playerData.uuid] = newData
        return true
    }
    fun unregister(uuid: UUID) {
        dataMap.remove(uuid)
    }
}