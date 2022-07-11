package kr.dreamstory.shop.prize

import kr.dreamstory.library.economy.PaymentType
import java.text.DecimalFormat

class Price(val type: PaymentType, val toBuy: Long?, val toSell: Long?, val stackable: Boolean) {
    companion object {
        fun PaymentType.getStringFormat(balance: Long): String {
            val decimalFormat = DecimalFormat("#,###").format(balance)
            return when(this) {
                PaymentType.MONEY -> "$decimalFormat 원"
                PaymentType.CASH -> "$decimalFormat 캐시"
            }
        }

        fun PaymentType.getColorFormat(string: String) = when(this) {
            PaymentType.MONEY -> "§e"
            PaymentType.CASH -> "§d"
        } + string

        fun PaymentType.format(balance: Long) = getColorFormat(getStringFormat(balance))
    }
}