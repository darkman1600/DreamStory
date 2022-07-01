package kr.dreamstory.library

import com.dreamstory.library.gui.DSGUIListener
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.utils.SystemTimeManager
import kr.dreamstory.library.item.dreamstory.item.DSItemCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DSLibrary: JavaPlugin() {
    override fun onEnable() {
        main = this
        DSItemManager.load(Bukkit.getConsoleSender())
        getCommand("dsitem")!!.setExecutor(DSItemCommand())
        server.pluginManager.registerEvents(DSGUIListener(), this)
        SystemTimeManager.startTask()
    }
}