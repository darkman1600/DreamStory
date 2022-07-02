package com.dreamstory.ability.socket.observer

import kr.dreamstory.ability.core.main.DSAbility
import com.dreamstory.ability.socket.packet.Packet
import com.dreamstory.ability.socket.packet.PacketType
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
import org.bukkit.Server
import java.io.DataOutputStream
import java.net.Socket

class WriteObserver(socket: Socket, core: DSAbility, server: Server): Observer(socket, core, server) {

    companion object {
        val packetMap = ArrayList<Packet>()
    }

    override fun run() {
        sc.schedule(core, SynchronizationContext.ASYNC) {
            output = DataOutputStream(socket.getOutputStream())
            while(true) {
                if (packetMap.isNotEmpty()) {
                    val list = ArrayList<Packet>(packetMap)
                    packetMap.clear()
                    list.forEach {
                        it.send()
                    }
                }
                waitFor(1)
            }
        }
    }

    fun sendPacket(type: PacketType, obj: String, target: String? = null) {
        packetMap.add(Packet(this,type,target, obj))
    }

}