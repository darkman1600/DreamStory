package kr.dreamstory.community.chat

import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.data.PlayerDataManger
import java.util.UUID

object CommunityManager {
    private val stateMap = HashMap<UUID, CommunityData>()
    fun getState(uuid: UUID, offline: Boolean = false): CommunityData {
        return if(offline) {
            CommunityData(uuid)
        } else {
            val state = stateMap[uuid]
            if(state == null) {
                val newState = CommunityData(uuid)
                stateMap[uuid] = newState
                stateMap[uuid]!!
            } else {
                state
            }
        }
    }
    fun register(uuid: UUID) {
        stateMap[uuid] = CommunityData(uuid)
    }
    fun unregister(uuid: UUID) {
        stateMap.remove(uuid)
    }
}