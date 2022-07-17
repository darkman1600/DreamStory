package kr.dreamstory.library.extension

import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.economy.Payment
import kr.dreamstory.library.economy.PaymentType
import kr.dreamstory.library.economy.WalletManager
import org.bukkit.entity.Player

fun Player.payment(type: PaymentType): Payment? = database.getPayment(type)

val Player.database
    get() = PlayerDataManger.getPlayerData(uniqueId)
