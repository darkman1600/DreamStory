package kr.dreamstory.library.data

import java.util.UUID

object PlayerDataManger {
    private val dataMap = HashMap<UUID,PlayerData>()

    fun getPlayerData(uuid: UUID) = dataMap[uuid] ?: getNewPlayerData(uuid)
    internal fun getNewPlayerData(uuid: UUID): PlayerData {
        dataMap[uuid] = PlayerData(uuid)
        return dataMap[uuid]!!
    }
}