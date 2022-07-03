package com.dreamstory.ability.socket.packet

import com.dreamstory.ability.extension.fromJson
import com.dreamstory.ability.manager.ChannelManager
import com.dreamstory.ability.socket.obj.SocketComponent
import com.dreamstory.ability.socket.observer.Observer
import com.dreamstory.ability.socket.observer.ReadObserver
import com.dreamstory.ability.socket.observer.WriteObserver

class Packet(
    private val observer: Observer,
    var packetType: PacketType? = null,
    var target: String? = null,
    var obj: String? = null
) {

    fun send() {
        if(observer is ReadObserver) return
        observer.output.writeInt(packetType!!.id)
        if(target != null) observer.output.writeUTF(target!!)
        observer.output.writeUTF(obj!!)
        observer.output.flush()
    }

    fun read() {
        if(observer is WriteObserver) return

        val id = observer.input.readInt()
        when(PacketType.byteBy(id)) {
            PacketType.NONE -> {
                return
            }
            PacketType.COMMAND_BROADCAST -> {
                val command = observer.input.readUTF()
                observer.server.dispatchCommand(observer.server.consoleSender, command)
            }
            PacketType.COMMAND_PLAYER -> {
                val p = observer.server.getPlayer(observer.input.readUTF())
                val command = observer.input.readUTF()
                p?.performCommand(command)
            }
            PacketType.COMMAND_SERVER -> {
                val serverName = observer.input.readUTF()
                val command = observer.input.readUTF()
                if(ChannelManager.name == serverName || ChannelManager.type.name == serverName) {
                    observer.server.dispatchCommand(observer.server.consoleSender, command)
                }
            }
            PacketType.MESSAGE_BROADCAST -> {
                val message = observer.input.readUTF()
                observer.server.onlinePlayers.forEach { it.sendMessage(message) }
            }
            PacketType.MESSAGE_PLAYER -> {
                val p = observer.server.getPlayer(observer.input.readUTF())
                val message = observer.input.readUTF()
                p?.sendMessage(message)
            }
            PacketType.MESSAGE_SERVER -> {
                val serverName = observer.input.readUTF()
                val message = observer.input.readUTF()
                if(ChannelManager.name == serverName || ChannelManager.type.name == serverName) {
                    observer.server.onlinePlayers.forEach { it.sendMessage(message) }
                }
            }
            PacketType.COMPONENT_BROADCAST -> {
                val message = observer.input.readUTF()
                val temp: SocketComponent = message.fromJson()
                val com = temp.build()
                observer.server.onlinePlayers.forEach { it.sendMessage(com) }
            }
            PacketType.COMPONENT_SERVER -> {
                val serverName = observer.input.readUTF()
                val message = observer.input.readUTF()
                val temp: SocketComponent = message.fromJson()
                val com = temp.build()
                if(ChannelManager.name == serverName || ChannelManager.type.name == serverName) {
                    observer.server.onlinePlayers.forEach { it.sendMessage(com) }
                }
            }
            PacketType.COMPONENT_PLAYER -> {
                val p = observer.server.getPlayer(observer.input.readUTF())
                val message = observer.input.readUTF()
                val temp: SocketComponent = message.fromJson()
                val com = temp.build()
                p?.sendMessage(com)
            }
        }
    }

}