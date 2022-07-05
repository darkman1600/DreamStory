package kr.dreamstory.library.economy

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.main
import java.util.UUID

object EconomyManager {

    private val economyMap = HashMap<UUID, Economy>()

    fun getEconomy(uuid: UUID) = economyMap[uuid]!!

    fun inputData(uuid: UUID) {
        val data = PlayerDataManger.getPlayerData(uuid)
        economyMap[uuid] = Economy(
            Money(data.getLong(main,"economy.money")),
            Cash(data.getLong(main,"economy.cash"))
        )
    }
    fun saveAndQuit(uuid: UUID) {
        val economy = getEconomy(uuid)
        val d = PlayerDataManger.getPlayerData(uuid)
        d.set(main,"economy.money",economy.money)
        d.set(main,"economy.cash",economy.cash)
        economyMap.remove(uuid)
    }

    fun saveAll() {
        main.server.scheduler.schedule(main,SynchronizationContext.ASYNC) {
            economyMap.forEach { (uuid, e) ->
                val d = PlayerDataManger.getPlayerData(uuid)
                d.set(main,"economy.money",e.money.amount)
                d.set(main,"economy.cash",e.cash.amount)
                d.save()
            }
        }
    }

}