package kr.dreamstory.library.economy

import java.text.DecimalFormat

class Money(money: Long = 0) {
    var amount = money
        private set

    fun add(amount: Long) {
        if(this.amount > Long.MAX_VALUE)
            this.amount += amount
    }
    fun remove(amount: Long) {
        if(amount > this.amount) this.amount = 0
        else this.amount -= amount
    }
    fun getStringFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(amount)
    }
}