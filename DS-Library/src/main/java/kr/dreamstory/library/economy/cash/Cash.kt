package kr.dreamstory.library.economy.cash

import java.text.DecimalFormat

class Cash {

    var cash: Long = 0
        private set

    fun add(amount: Long) {
        if(cash > Long.MAX_VALUE)
            cash += amount
    }
    fun remove(amount: Long) {
        if(amount > cash) cash = 0
        else cash -= amount
    }
    fun getDecimalFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(cash)
    }


}