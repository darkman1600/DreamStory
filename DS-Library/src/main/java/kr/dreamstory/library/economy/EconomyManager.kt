package kr.dreamstory.library.economy

import kr.dreamstory.library.economy.cash.Cash
import kr.dreamstory.library.economy.money.Money
import java.util.UUID

object EconomyManager {

    private val moneyMap = HashMap<UUID, Money>()
    private val cashMap = HashMap<UUID, Cash>()

    fun getMoney(uuid: UUID) = moneyMap[uuid] ?: getNewMoney(uuid)
    private fun getNewMoney(uuid: UUID): Money {
        moneyMap[uuid] = Money()
        return moneyMap[uuid]!!
    }

    fun getCash(uuid: UUID) = cashMap[uuid] ?: getNewCash(uuid)
    private fun getNewCash(uuid: UUID): Cash {
        cashMap[uuid] = Cash()
        return cashMap[uuid]!!
    }

}