package kr.dreamstory.library

import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.utils.SystemTimeManager
import kr.dreamstory.library.item.dreamstory.item.DSItemCommand
import kr.dreamstory.library.listener.LibraryListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DSLibrary: JavaPlugin() {
    override fun onEnable() {
        main = this
        DSItemManager.load(Bukkit.getConsoleSender())
        getCommand("dsitem")!!.setExecutor(DSItemCommand())
        server.pluginManager.registerEvents(LibraryListener(),this)
        SystemTimeManager.startTask()
        PlayerDataManger.saveTask()
    }

}