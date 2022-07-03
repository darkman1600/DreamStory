package kr.dreamstory.shop

import org.bukkit.plugin.java.JavaPlugin

class DSShop: JavaPlugin() {

    override fun onEnable() {
        main = this
        getCommand("상점")?.setExecutor(Command())
        server.pluginManager.registerEvents(Listener,main)
        DataLoader.load()
    }

    override fun onDisable() {
    }

}