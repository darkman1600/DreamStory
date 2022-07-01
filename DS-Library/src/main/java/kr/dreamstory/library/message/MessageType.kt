package kr.dreamstory.library.message

enum class MessageType(val prefix: String) {
    SUCCESS("§a\u2714§r"),
    DEFAULT("§e!§r"),
    WARNING("§c!§r"),
    DANGER("§4!§r")
}