package kr.dreamstory.library.economy

import java.text.DecimalFormat

class Cash(cash: Long = 0) {
    var amount = cash
        private set

    fun set(amount: Long) {
        this.amount = amount
    }
    fun add(amount: Long) {
        this.amount += amount
    }
    fun remove(amount: Long) {
        this.amount -= amount
    }

    fun hasAmount(amount: Long): Boolean {
        return this.amount >= amount
    }

    fun getStringFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(amount)
    }
}