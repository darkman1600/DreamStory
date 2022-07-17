package kr.dreamstory.community

import kr.dreamstory.community.command.ChatModeCommand
import kr.dreamstory.community.command.IgnoreCommand
import kr.dreamstory.community.command.FriendCommand
import kr.dreamstory.community.command.op.FriendOpCommand
import kr.dreamstory.community.listener.ChatListener
import kr.dreamstory.community.listener.FriendListener
import kr.dreamstory.community.prefix.PrefixManager
import org.bukkit.plugin.java.JavaPlugin

class DSCommunity: JavaPlugin() {
    override fun onEnable() {
        main = this
        PrefixManager.loadPrefix()
        server.pluginManager.registerEvents(ChatListener(),this)
        server.pluginManager.registerEvents(FriendListener(),this)
        getCommand("채팅모드")!!.setExecutor(ChatModeCommand())
        getCommand("차단")!!.setExecutor(IgnoreCommand())
        getCommand("친구")!!.setExecutor(FriendCommand())
        getCommand("친구관리")!!.setExecutor(FriendOpCommand())
    }
}