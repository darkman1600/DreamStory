package kr.dreamstory.library.extension

import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.economy.EconomyManager
import org.bukkit.entity.Player

val Player.economy
    get() = EconomyManager.getEconomy(uniqueId)

val Player.database
    get() = PlayerDataManger.getPlayerData(uniqueId)
