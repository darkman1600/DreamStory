package kr.dreamstory.library.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import org.bukkit.Material
import org.bukkit.block.Skull
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

object PlayerDataManger {
    private val dataMap = HashMap<UUID,PlayerData>()

    fun getPlayerData(uuid: UUID,offline: Boolean = false): PlayerData {
        return if(offline) {
            PlayerData(uuid)
        } else {
            val data = dataMap[uuid]
            if(data == null) {
                val newData = PlayerData(uuid)
                dataMap[uuid] = newData
                dataMap[uuid]!!
            } else {
                data
            }
        }
    }
    fun getOfflinePlayerData(uuid: UUID): PlayerData = PlayerData(uuid)
    fun register(player: Player) {
        val uuid = player.uniqueId
        val newData = PlayerData(uuid)
        newData.setName(player.name)

        if(!newData.hasPlayedBefore) {
            val headBase = ItemStack(Material.PLAYER_HEAD)
            val headMeta = headBase.itemMeta as SkullMeta
            headMeta.owningPlayer = player
            headBase.itemMeta = headMeta
            newData.setHead(headBase)
        }

        dataMap[uuid] = newData
    }

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
                main.server.broadcastMessage("저장")
                saveAll()
            }
        }
    }
}