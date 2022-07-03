package com.dreamstory.ability.socket.packet

enum class PacketType(val id: Int) {

    COMMAND_BROADCAST(0),
    COMMAND_PLAYER(1),
    COMMAND_SERVER(2),
    MESSAGE_BROADCAST(3),
    MESSAGE_PLAYER(4),
    MESSAGE_SERVER(5),
    COMPONENT_BROADCAST(6),
    COMPONENT_PLAYER(7),
    COMPONENT_SERVER(8),
    NONE(9);


    companion object {
        fun byteBy(id: Int): PacketType = values().firstOrNull { v: PacketType -> v.id == id }?: NONE
    }
}