package kr.dreamstory.library.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import kr.dreamstory.library.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

object PlayerDataManger {
    private val dataMap = HashMap<UUID,PlayerData>()

    fun getPlayerData(uuid: UUID) = dataMap[uuid] ?: register(uuid)
    fun register(uuid: UUID): PlayerData? {
        return try {
            dataMap[uuid] = PlayerData(uuid)
            dataMap[uuid]
        } catch (e: Exception) {
            val player = Bukkit.getPlayer(uuid)
            val name = player?.name ?: uuid.toString()
            MessageManager.pluginMessage(main,"[ §e$name §f] 님의 데이터를 로드하는데 실패하였습니다.")
            player?.kickPlayer("데이터를 불러오는데 실패하였습니다.\n같은 현상이 반복될 경우,\n관리자에게 문의하세요.")
            null
        }
    }
    fun unregister(uuid: UUID) { dataMap.remove(uuid) }

    private val quitSet = HashSet<UUID>()

    fun saveAndQuit(player: Player) {
        val uuid = player.uniqueId
        if(quitSet.contains(uuid)) return
        quitSet.add(uuid)
        val data = dataMap[uuid] ?: return
        main.schedule(SynchronizationContext.ASYNC) {
            data.update()
            waitFor(200)
            if(!player.isOnline) {
                data.save()
            }
            quitSet.remove(uuid)
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