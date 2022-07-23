package kr.dreamstory.data_loader

import kr.dreamstory.data_loader.listener.LoadingDetectListener
import org.bukkit.plugin.java.JavaPlugin

class DSDataLoader: JavaPlugin() {
    override fun onEnable() {
        main = this
        server.pluginManager.registerEvents(LoadingDetectListener(),this)
    }
}