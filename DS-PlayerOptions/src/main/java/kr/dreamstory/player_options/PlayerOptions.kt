package kr.dreamstory.player_options

import kr.dreamstory.player_options.command.OptionCommand
import kr.dreamstory.player_options.listener.UtilOptionListener
import org.bukkit.plugin.java.JavaPlugin

class PlayerOptions: JavaPlugin() {
    override fun onEnable() {
        main = this
        getCommand("설정")!!.setExecutor(OptionCommand())
        server.pluginManager.registerEvents(UtilOptionListener(),this)
    }
}