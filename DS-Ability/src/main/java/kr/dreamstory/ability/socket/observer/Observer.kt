package com.dreamstory.ability.socket.observer

import kr.dreamstory.ability.core.main.DSAbility
import org.bukkit.Server
import org.bukkit.scheduler.BukkitScheduler
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

abstract class Observer(val socket: Socket, val core: DSAbility, val server: Server) {

    lateinit var sc: BukkitScheduler
    lateinit var input: DataInputStream
    lateinit var output: DataOutputStream

    fun start(sc: BukkitScheduler) {
        this.sc = sc
        run()
    }

    abstract fun run()

    fun close() {
        try { input.close() } catch (e: Exception) {}
        try { output.close() } catch (e: Exception) {}
        try { socket.close()  } catch (e: Exception) {}
    }

}