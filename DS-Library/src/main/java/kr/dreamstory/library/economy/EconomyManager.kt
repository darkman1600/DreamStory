package kr.dreamstory.library.economy

import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.main
import java.util.UUID

object EconomyManager {

    private val economyMap = HashMap<UUID, Economy>()

    fun getEconomy(uuid: UUID) = economyMap[uuid]!!

    internal fun register(uuid: UUID) {
        val data = PlayerDataManger.getPlayerData(uuid)
        economyMap[uuid] = Economy(data.getLong(main,"economy.money"),data.getLong(main,"economy.cash"))
    }

}