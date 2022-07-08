package kr.dreamstory.library.economy

import kr.dreamstory.library.economy.objs.Cash
import kr.dreamstory.library.economy.objs.Money
import org.bukkit.inventory.meta.ItemMeta

class Wallet(
    money: Long,
    cash: Long
) {

    private val data = HashMap<PaymentType,Payment>()

    init {
        data[PaymentType.MONEY] = Money(money,PaymentType.MONEY)
        data[PaymentType.CASH] = Cash(cash,PaymentType.CASH)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "type에 넣는 값과 대조되는 Payment자식 클래스가 있는지 확인.")
    fun getPayment(type: PaymentType) = data[type]
}