package kr.dreamstory.library.economy

import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType


/**
 * [PaymentType] 이 추가될 경우, [Payment] 를 상속받는 클래스를 꼭 추가하세요!
 */

enum class PaymentType {
    MONEY,
    CASH;

    companion object {
        fun fromString(type: String) = typeMap[type]
        private val typeMap = HashMap<String, PaymentType>()
        init {
            PaymentType.values().forEach {
                typeMap[it.name] = it
            }
        }
    }
}