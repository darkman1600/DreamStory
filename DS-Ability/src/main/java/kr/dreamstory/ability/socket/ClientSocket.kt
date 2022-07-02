package com.dreamstory.ability.socket

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.extension.toJson
import com.dreamstory.ability.socket.obj.SocketComponent
import com.dreamstory.ability.socket.observer.ReadObserver
import com.dreamstory.ability.socket.observer.WriteObserver
import com.dreamstory.ability.socket.packet.PacketType
import java.net.InetAddress
import java.net.Socket
import java.util.logging.Level

object ClientSocket {

    private const val ADDRESS = "182.209.99.2"
    private const val LOCAL = "127.0.0.1"
    private const val port = 25560
    private var socket: Socket? = null
    private val logger by lazy { main.server.logger }
    private lateinit var readObserver: ReadObserver
    private lateinit var writeObserver: WriteObserver

    @Deprecated(level = DeprecationLevel.WARNING, message = "사용하지 마세용.")
    fun run() {
        var ia = InetAddress.getByName(ADDRESS)
        try {
            socket = Socket(ia, port)
        } catch (e: Exception) {
            logger.log(Level.INFO, "아이피 접속 실패로 로컬 서버로 연결됩니다.")
            ia = InetAddress.getByName(LOCAL)
            try {
                socket = Socket(ia, port)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "소켓 서버 접속에 실패했습니다.")
            }
        }
        if(socket == null) return
        val sc = main.server.scheduler
        writeObserver = WriteObserver(socket!!, main, main.server)
        writeObserver.start(sc)
        readObserver = ReadObserver(socket!!, main, main.server)
        readObserver.start(sc)
    }

    fun sendMessage(targetName: String,message: String) { writeObserver.sendPacket(PacketType.MESSAGE_PLAYER,message,targetName) }
    fun sendServerMessage(serverNameOrType: String,message: String) { writeObserver.sendPacket(PacketType.MESSAGE_SERVER, message, serverNameOrType) }
    fun sendBroadCastMessage(message: String) { writeObserver.sendPacket(PacketType.MESSAGE_BROADCAST, message) }

    fun sendComponentMessage(targetName: String,message: SocketComponent) { writeObserver.sendPacket(PacketType.COMPONENT_PLAYER,message.toJson(),targetName) }
    fun sendComponentServerMessage(serverNameOrType: String,message: SocketComponent) { writeObserver.sendPacket(
        PacketType.COMPONENT_SERVER, message.toJson(), serverNameOrType) }
    fun sendComponentBroadCastMessage(message: SocketComponent) { writeObserver.sendPacket(PacketType.COMPONENT_BROADCAST, message.toJson()) }

    fun sendCommand(targetName: String,command: String) { writeObserver.sendPacket(PacketType.COMMAND_PLAYER,command,targetName) }
    fun sendServerCommand(serverNameOrType: String,command: String) { writeObserver.sendPacket(PacketType.COMMAND_SERVER, command, serverNameOrType) }
    fun sendBroadCastCommand(command: String) { writeObserver.sendPacket(PacketType.COMMAND_BROADCAST, command) }

    fun close() { writeObserver.close(); readObserver.close() }

}