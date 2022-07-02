package com.dreamstory.ability.manager

import com.dreamstory.ability.ability.channel.ChannelType
import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.extension.toJson
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import world.bentobox.bentobox.BentoBox
import java.sql.PreparedStatement

object ChannelManager {

    val type = ChannelType.valueOf(main.config.getString("server.type")?.toUpperCase() ?: "NONE")
    val name = main.config.getString("server.name")!!
    val port = main.server.port
    var status = ChannelStatus.E
    var islandCount = 0
        set(value) { field = if(value < 0) 0 else value }

    val isMainServer get() = type == ChannelType.SERVER

    fun removeChannel() {
        MysqlManager.executeQuery("DELETE FROM server WHERE port='$port'")
    }

    fun connectChannel(channelName: String,p: Player) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(channelName)
        p.sendPluginMessage(main, "BungeeCord", out.toByteArray())
    }

    @Deprecated(message = "메인에서만 실행", level = DeprecationLevel.WARNING)
    fun channelCheck(waitTime: Long): Boolean {
        val bool = when(type) {
            ChannelType.NONE -> {
                main.logger.info("§c채널 타입이 올바르지 않습니다.")
                false
            }
            ChannelType.ISLAND -> {
                if(main.server.pluginManager.getPlugin("BentoBox")==null) {
                    main.logger.info("§cISLAND 형식의 서버는 BentoBox 가 필요합니다.")
                    false
                } else true
            }
            else -> true
        }
        if(bool) {
            try {
                main.server.scheduler.schedule(main) {
                    waitFor(10)
                    if (!isMainServer) MysqlManager.executeQuery("INSERT INTO server(port,name,type,players,status) values ($port,'$name','${type.name}','${HashSet<Int>().toJson()}','${BentoBox.getInstance().islands.islandCount}')")
                    else MysqlManager.executeQuery("INSERT INTO server(port,name,type,players,status) values ($port,'$name','${type.name}','${HashSet<Int>().toJson()}','${status.name}')")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            if(ChannelType.ISLAND == type) return true
            main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
                waitFor(waitTime)
                repeating(100)
                var temp: PreparedStatement? = null
                while(true) {
                    val playerCount = main.server.onlinePlayers.size
                    val tps = main.server.tps[0]
                    val status: ChannelStatus = if (playerCount > 200) ChannelStatus.E
                    else if (tps < 8) ChannelStatus.E
                    else if (tps < 12) ChannelStatus.D
                    else if (tps < 16) ChannelStatus.C
                    else {
                        if (playerCount > 100) ChannelStatus.B
                        else ChannelStatus.A
                    }
                    try {
                        temp = MysqlManager.connection!!.prepareStatement("UPDATE server SET status='${status.name}' WHERE port='$port'")
                        temp.execute()
                        temp.close()
                    } catch (e: Exception) {
                        main.logger.info("§c쿼리 접속에 실패하여, 서버 정보 업데이트가 불가합니다.")
                    } finally {
                        temp?.close()
                    }
                    yield()
                }
            }
        }
        return bool
    }

}