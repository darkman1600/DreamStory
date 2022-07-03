package com.dreamstory.ability.socket.observer

import kr.dreamstory.ability.core.main.DSAbility
import com.dreamstory.ability.socket.packet.Packet
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
import org.bukkit.Server
import java.io.DataInputStream
import java.net.Socket

class ReadObserver(socket: Socket, core: DSAbility, server: Server): Observer(socket, core, server) {

    override fun run() {
        sc.schedule(core, SynchronizationContext.ASYNC) {
            input = DataInputStream(socket.getInputStream())
            while (true) {
                Packet(this@ReadObserver).read()
                waitFor(1)
            }
        }
    }

}