package kr.dreamstory.library.economy.objs

import kr.dreamstory.library.economy.Payment
import kr.dreamstory.library.economy.PaymentType
import java.text.DecimalFormat

class Cash(
    balance: Long = 0,
    type: PaymentType
): Payment(
    balance,
    type
) {

}