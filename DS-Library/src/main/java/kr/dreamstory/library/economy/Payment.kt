package kr.dreamstory.library.economy

import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.main
import org.bukkit.configuration.file.YamlConfiguration
import java.text.DecimalFormat

/**
 * [Payment] 를 상속받는 클래스가 추가 될 경우, [PaymentType]을 꼭 추가하세요.
 */

abstract class Payment(
    balance: Long,
    private val type: PaymentType
) {

    var balance = balance
        private set

    fun set(amount: Long) { balance = amount }
    fun add(amount: Long) { balance += amount }

    fun safeRemove(amount: Long): Boolean {
        if(balance < amount) return false
        balance -= amount
        return true
    }
    fun unSafeRemove(amount: Long) { balance -= amount }

    fun getStringFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(balance)
    }

}