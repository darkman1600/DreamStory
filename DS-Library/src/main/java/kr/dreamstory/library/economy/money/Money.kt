package kr.dreamstory.library.economy.money

import java.text.DecimalFormat

class Money {

    var money: Long = 0
        private set

    fun addMoney(amount: Long) {
        if(money > Long.MAX_VALUE)
        money += amount
    }
    fun removeMoney(amount: Long) {
        if(amount > money) money = 0
        else money -= amount
    }
    fun getMoneyFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(money)
    }

}