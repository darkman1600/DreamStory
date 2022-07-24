package kr.dreamstory.library.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import org.bukkit.entity.Player
import java.util.*

object PlayerDataManger {
    private val dataMap = HashMap<UUID,PlayerData>()

    fun getPlayerData(uuid: UUID): PlayerData? = dataMap[uuid]

    @Deprecated("동기 스레드에서 사용하지 마세요.", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith(""))
    fun getOfflinePlayerData(uuid: UUID): PlayerData? {
        val d = dataMap[uuid]
        return if(d == null) {
            val new = PlayerData(uuid)
            if(!new.hasPlayedBefore) return null
            dataMap[uuid] = new
            dataMap[uuid]!!
        } else {
            d
        }
    }

    fun loadPlayerData(player: Player): Boolean {
        val uuid = player.uniqueId
        val d = getPlayerData(uuid)
        if(d == null) {
            val newData = PlayerData(uuid)
            newData.joinUpdate(player)
            dataMap[uuid] = newData
        }
        return true
    }

    fun saveAndQuit(player: Player) {
        val uuid = player.uniqueId
        val data = dataMap[uuid] ?: return
        main.schedule(SynchronizationContext.ASYNC) {
            data.update()
            data.save()
            dataMap.remove(uuid)
        }
    }

    fun updateAll() {
        main.schedule(SynchronizationContext.ASYNC) {
            dataMap.values.forEach { it.update() }
        }
    }
    private fun saveAll() {
        main.schedule(SynchronizationContext.ASYNC) {
            dataMap.values.forEach { it.save() }
        }
    }

    fun saveTask() {
        main.server.scheduler.schedule(main,SynchronizationContext.ASYNC) {
            while(true) {
                waitFor(160)
                DataUpdateEvent().callEvent()
                waitFor(40)
                DataSaveEvent().callEvent()
                saveAll()
            }
        }
    }
}