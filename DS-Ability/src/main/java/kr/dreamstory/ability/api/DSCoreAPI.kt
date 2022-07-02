package kr.dreamstory.ability.api

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.extension.fromJson
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.socket.ClientSocket
import com.dreamstory.ability.socket.obj.SocketComponent
import com.dreamstory.ability.util.SignMenuFactory
import com.google.common.io.ByteStreams
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.entity.Player
import java.util.*

object DSCoreAPI {

    private val idMap = HashMap<UUID,Int>()
    private val sc by lazy { main.server.scheduler }
    val signMenuFactory by lazy { SignMenuFactory(main) }

    private var idUpdate = true
    private var nameUpdate = true

    fun sendMessage(targetName: String,message: String) { ClientSocket.sendMessage(targetName,message) }
    fun sendServerMessage(serverNameOrType: String,message: String) { ClientSocket.sendServerMessage(serverNameOrType, message) }
    fun sendBroadCastMessage(message: String) { ClientSocket.sendBroadCastMessage(message) }

    fun sendComponentMessage(targetName: String,message: SocketComponent) { ClientSocket.sendComponentMessage(targetName,message) }
    fun sendComponentServerMessage(serverNameOrType: String,message: SocketComponent) { ClientSocket.sendComponentServerMessage(serverNameOrType, message) }
    fun sendComponentBroadCastMessage(message: SocketComponent) { ClientSocket.sendComponentBroadCastMessage(message) }

    fun sendCommand(targetName: String,command: String) { ClientSocket.sendCommand(targetName,command) }
    fun sendServerCommand(serverNameOrType: String,command: String) { ClientSocket.sendServerCommand(serverNameOrType, command) }
    fun sendBroadCastCommand(command: String) { ClientSocket.sendBroadCastCommand(command) }


    private var onlinePlayerSQLNames: HashSet<String> = HashSet()
    fun getOnlinePlayerSQLNames(async: Boolean = true): HashSet<String> {
        if(nameUpdate) {
            val temp = HashSet<String>()
            nameUpdate = false
            if(async) {
                sc.schedule(main, SynchronizationContext.ASYNC) {
                    getOnlinePlayerSQL(false).forEach {
                        val s: String? = getCustomName(it)
                        if(s!=null) temp.add(s)
                    }
                    waitFor(200)
                    nameUpdate = true
                }
            } else {
                getOnlinePlayerSQL(false).forEach {
                    val s: String? = getCustomName(it)
                    if (s != null) temp.add(s)
                }
                onlinePlayerSQLNames = temp
                sc.schedule(main, SynchronizationContext.ASYNC) {
                    waitFor(200)
                    nameUpdate = true
                }
            }
        }
        return onlinePlayerSQLNames
    }

    private var onlinePlayersSQL: HashSet<Int> = HashSet()
    fun getOnlinePlayerSQL(async: Boolean = true): HashSet<Int> {
        if(idUpdate) {
            val temp = HashSet<Int>()
            idUpdate = false
            if(async) {
                sc.schedule(main, SynchronizationContext.ASYNC) {
                    val s = MysqlManager.connection!!.prepareStatement("SELECT players FROM server WHERE 1")
                    val set = s.executeQuery()
                    while (set.next()) {
                        temp.addAll(set.getString("players").fromJson())
                    }
                    set.close()
                    s.close()
                    onlinePlayersSQL = temp
                    waitFor(200)
                    idUpdate = true
                }
            } else {
                val s = MysqlManager.connection!!.prepareStatement("SELECT players FROM server WHERE 1")
                val set = s.executeQuery()
                while (set.next()) { temp.addAll(set.getString("players").fromJson()) }
                set.close()
                s.close()
                onlinePlayersSQL = temp
                sc.schedule(main, SynchronizationContext.ASYNC) {
                    waitFor(200)
                    idUpdate = true
                }
            }
        }
        return onlinePlayersSQL
    }

    private var onlinePlayersLocal: HashSet<Int> = HashSet()
    private val onlinePlayersLocalNames: HashSet<String> = HashSet()
    fun getOnlinePlayersLocal(): HashSet<Int> =
        onlinePlayersLocal
    fun getOnlinePlayersLocalNames(): HashSet<String> =
        onlinePlayersLocalNames
    fun addOnlinePlayerLocal(id: Int,name: String?) {
        onlinePlayersLocal.add(id)
        if(name != null) onlinePlayersLocalNames.add(name)
    }
    fun removeOnlinePlayerLocal(id: Int,name: String?) {
        onlinePlayersLocal.remove(id)
        if(name != null) onlinePlayersLocalNames.remove(name)
    }

    fun getPlayerId(uuid: UUID): Int {
        if(idMap.containsKey(uuid)) return idMap[uuid]!!
        val result:Int? = MysqlManager.executeQuery("player","id","uuid", uuid)
        if (result?.equals(0) == false) {
            idMap[uuid] = result
            return result
        }
        return 0
    }

    fun getCustomName(playerId: Int): String? = MysqlManager.executeQuery("player","name","id",playerId)
    fun getRealName(targetId: Int): String? = MysqlManager.executeQuery("player","real_name","id",targetId)

    fun sendBungeeMessage(source: Player, targetId: Int, message: String) {
        val realName = getRealName(targetId) ?: return
        sendBungeeMessage(source, realName, message)
    }

    fun sendRawBungeeMessage(source: Player, targetId: Int, message: String) {
        val realName = getRealName(targetId) ?: return
        sendBungeeMessage(source, realName, message, true)
    }

    private fun sendBungeeMessage(source: Player, targetRealName: String, message: String,raw: Boolean = false) {
        val out = ByteStreams.newDataOutput()
        if(raw) out.writeUTF("MessageRaw")
        else out.writeUTF("Message")
        out.writeUTF(targetRealName)
        out.writeUTF(message)
        source.sendPluginMessage(main, "BungeeCord", out.toByteArray())
    }


}