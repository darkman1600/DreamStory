package kr.dreamstory.player_options

import kr.dreamstory.player_options.command.OptionCommand
import org.bukkit.plugin.java.JavaPlugin

class PlayerOptions: JavaPlugin() {
    override fun onEnable() {
        getCommand("설정")!!.setExecutor(OptionCommand())
    }
}