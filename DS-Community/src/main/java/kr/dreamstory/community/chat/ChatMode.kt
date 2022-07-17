package kr.dreamstory.community.chat

import kr.dreamstory.library.economy.PaymentType
import net.kyori.adventure.text.format.TextColor

enum class ChatMode(
    val display: String,
    val color: TextColor
) {
    DEFAULT("전체",TextColor.fromCSSHexString("#8d8d8d")!!),
    FRIENDS("친구",TextColor.fromCSSHexString("#d9ce52")!!),
    ISLAND("섬",TextColor.fromCSSHexString("#6dff54")!!),
    REGION("지역",TextColor.fromCSSHexString("#f97e24")!!);

    companion object {
        fun fromString(type: String) = typeMap[type]
        private val typeMap = HashMap<String, ChatMode>()
        init {
            values().forEach {
                typeMap[it.name] = it
            }
        }
    }
}