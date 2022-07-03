package kr.dreamstory.library.extension

import kr.dreamstory.library.economy.EconomyManager
import org.bukkit.entity.Player

val Player.money
    get() = EconomyManager.getMoney(uniqueId)
val Player.cash
    get() = EconomyManager.getCash(uniqueId)
