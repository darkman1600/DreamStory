package kr.dreamstory.shop.prize

enum class PriceType {
    MONEY,
    CASH;
    companion object {
        fun get(key: String) = PriceType.values().firstOrNull { it.name == key.uppercase() }
    }
}