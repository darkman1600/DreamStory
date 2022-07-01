package kr.dreamstory.library

import org.bukkit.plugin.java.JavaPlugin

class DSLibrary: JavaPlugin() {
    override fun onEnable() {
        main = this
    }
}