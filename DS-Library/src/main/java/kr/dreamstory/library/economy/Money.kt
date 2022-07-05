package kr.dreamstory.library.economy

import java.text.DecimalFormat

class Money(money: Long = 0) {
    var amount = money
        private set

    fun set(amount: Long) { this.amount = amount }
    fun add(amount: Long) { this.amount += amount }
    fun remove(amount: Long) { this.amount -= amount }

    fun tryRemove(amount: Long): Boolean {
        if(this.amount < amount) return false
        this.amount -= amount
        return true
    }

    fun hasAmount(amount: Long): Boolean {
        return this.amount >= amount
    }

    fun getStringFormat(): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(amount)
    }
}