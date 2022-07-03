package kr.dreamstory.library.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import java.util.UUID

object PlayerDataManger {
    private val dataMap = HashMap<UUID,PlayerData>()

    fun getPlayerData(uuid: UUID) = dataMap[uuid]!!
    internal fun getNewPlayerData(uuid: UUID): PlayerData {
        dataMap[uuid] = PlayerData(uuid)
        return dataMap[uuid]!!
    }

    fun unregister(uuid: UUID) { dataMap.remove(uuid) }

    fun saveTask() {
        main.server.scheduler.schedule(main,SynchronizationContext.ASYNC) {
            while(true) {
                waitFor(200)
                DataSaveEvent().callEvent()
            }
        }
    }
}