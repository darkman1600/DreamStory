package com.dreamstory.ability.extension

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.data.Friend
import kr.dreamstory.ability.ability.play.data.PlayerChatType
import kr.dreamstory.ability.ability.play.data.PlayerOption
import kr.dreamstory.ability.ability.play.data.WeatherType
import kr.dreamstory.ability.api.DSCoreAPI
import kr.dreamstory.ability.api.DSCoreAPI.getPlayerId
import com.dreamstory.ability.listener.island.IslandListener.Companion.dispatchIslandCommand
import com.dreamstory.ability.manager.ChannelManager
import com.dreamstory.ability.manager.ItemListManager
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.util.DSLocation
import com.google.common.io.ByteStreams
import com.mojang.authlib.GameProfile
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

val Player.id: Int get() = getPlayerId(uniqueId)
var Player.chatMode: PlayerChatType
    get() {
    val data:String = MysqlManager.executeQuery("player_option","chat_mode","player",id)?: return PlayerChatType.ALL
    return PlayerChatType.valueOf(data)
} set(value) {
    MysqlManager.executeQuery("UPDATE player_option SET chat_mode=$value WHERE id=$id")
}

var Player.wforestName: String
    get() = DSCoreAPI.getCustomName(id)?: name
    set(value) {
        setPlayerListName(value)
        setDisplayName(value)
        customName = value
        MysqlManager.executeQuery("UPDATE player SET name='$value' WHERE id=$id")
        server.onlinePlayers.forEach {
            if(it.uniqueId == uniqueId) return@forEach
            (it as CraftPlayer).handle.b.a(
                PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, (this as CraftPlayer).handle)
            )
            val profile = this.profile
            try {
                val nameField = GameProfile::class.java.getDeclaredField("name")
                nameField.isAccessible = true

                val modifiersField = Field::class.java.getDeclaredField("modifiers")
                modifiersField.isAccessible = true
                modifiersField.setInt(nameField, nameField.modifiers and Modifier.FINAL.inv())
                nameField.set(profile, value)
            } catch (e: Exception) {}
            //ADDS THE PLAYER
            it.handle.b.a(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, this.handle))
            it.handle.b.a(PacketPlayOutEntityDestroy(this.entityId))
            it.handle.b.a(PacketPlayOutNamedEntitySpawn(this.handle))
        }
    }

val Player.playerOption: PlayerOption
    get() {
        var option = PlayerOption.getPlayerOptionById(id)
        return if(option != null) {
            option
        } else {
            option = PlayerOption(id)
            option.make()
            option
        }
    }

fun Player.changeChannel(channel: String?): Boolean {
    if(channel?.equals(ChannelManager.name) != false) return false
    val out = ByteStreams.newDataOutput()
    out.writeUTF("Connect")
    out.writeUTF(channel)
    sendPluginMessage(main, "BungeeCord", out.toByteArray())
    return true
}

val Player.friends: Friend
    get() {
        val data: Friend? = Friend.getFriend(id)
        return if(data == null) {
            val friend = Friend(id)
            MysqlManager.executeQuery("INSERT INTO friend (player,friends) values ($id, '${friend.toJson()}'")
            Friend(id)
        }
        else data
    }

fun Player.updateSql() {
    MysqlManager.executeQuery("UPDATE player SET inv='${ItemListManager.itemStackArrayToBase64(inventory.contents)}'," +
            "last_location='${locationToDSLocationString(location)}', " +
            "last_connect=${Date().time}, " +
            "op=${isOp}, " +
            "hp=${health}, " +
            "maxHp=${maxHealth}, " +
            "hungry=${foodLevel}, " +
            "ban=${isBanned} " +
            "WHERE id=${id}")
}

fun Player.teleport(dest: DSLocation): Boolean {
    val option = playerOption
    if(dest.isChannel) {
        return if(ChannelManager.isMainServer) {
            gameMode = GameMode.ADVENTURE
            if(dest.location != null) teleport(dest.location)
            else teleport(world.spawnLocation)
            true
        } else {
            gameMode = GameMode.SURVIVAL
            // move default server
            false
        }
    } else {
        if(!ChannelManager.isMainServer) {
            gameMode = GameMode.SURVIVAL
            return if(dest.islandServerName == ChannelManager.name) {
                if(option.weather != WeatherType.SUN) setPlayerWeather(org.bukkit.WeatherType.DOWNFALL)
                setPlayerTime(option.time.tick,false)
                if(dest.location != null) teleport(dest.location)
                else {
                    println("make is")
                    dispatchIslandCommand("island")
                }
                true
            } else {
                changeChannel(dest.islandServerName)
                false
            }
        } else {
            gameMode = GameMode.ADVENTURE
            return if(dest.islandServerName == null) {
                teleport(world.spawnLocation)
                println("call2")
                true
            } else {
                changeChannel(dest.islandServerName)
                false
            }
        }
    }
}