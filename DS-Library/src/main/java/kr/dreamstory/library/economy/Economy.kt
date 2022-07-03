package kr.dreamstory.library.economy

import java.text.DecimalFormat

class Economy(
    private var money: Long = 0,
    private var cash: Long = 0
) {

    fun getMoney(): Long = money

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

    fun getCash(): Long = cash

    fun addCash(amount: Long) {
        if(cash > Long.MAX_VALUE)
            cash += amount
    }
    fun removeCash(amount: Long) {
        if(amount > cash) cash = 0
        else cash -= amount
    }
    fun getCashFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(cash)
    }

}