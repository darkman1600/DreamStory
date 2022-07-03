package kr.dreamstory.library.data

import kr.dreamstory.library.main
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class PlayerData(uuid: UUID) {

    private val file = File("${main.dataFolder.path}\\database\\player_data","$uuid")
    private val config = YamlConfiguration.loadConfiguration(file)

    fun getInt(plugin: JavaPlugin, key: String): Int { return config.getInt("${plugin.name.lowercase()}.$key") }
    fun getLong(plugin: JavaPlugin, key: String): Long { return config.getLong("${plugin.name.lowercase()}.$key") }
    fun getString(plugin: JavaPlugin, key: String): String? { return config.getString("${plugin.name.lowercase()}.$key") }
    fun getStringList(plugin: JavaPlugin, key: String): List<String> { return config.getStringList("${plugin.name.lowercase()}.$key") }

    fun set(plugin: JavaPlugin, key: String, value: Any?, save: Boolean = false) {
        config.set("${plugin.name.lowercase()}.$key",value)
        if(save) save()
    }

    fun save() { config.save(file) }

}