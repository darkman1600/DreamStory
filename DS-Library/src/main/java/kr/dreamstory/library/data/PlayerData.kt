package kr.dreamstory.library.data

import kr.dreamstory.library.economy.Payment
import kr.dreamstory.library.economy.PaymentType
import kr.dreamstory.library.economy.Wallet
import kr.dreamstory.library.economy.objs.Money
import kr.dreamstory.library.main
import kr.dreamstory.library.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class PlayerData(val uuid: UUID) {

    private val file = File("${main.dataFolder.path}\\database\\player_data","$uuid")
    private val dataMap = YamlConfiguration.loadConfiguration(file)

    fun getInt(plugin: JavaPlugin,key: String, default: Int = 0) = dataMap.getInt(plugin.name.lowercase() + "." + key, default)
    fun getIntOrNull(plugin: JavaPlugin, key: String): Int? {
        val str = dataMap.getString(plugin.name.lowercase() + "." + key) ?: return null
        return str.toIntOrNull()
    }

    fun getLong(plugin: JavaPlugin, key: String, default: Long = 0): Long = dataMap.getLong(plugin.name.lowercase() + "." + key,default)
    fun getLongOrNull(plugin: JavaPlugin, key: String): Long? {
        val str = dataMap.getString(plugin.name.lowercase() + "." + key) ?: return null
        return str.toLongOrNull()
    }

    fun getStringOrNull(plugin: JavaPlugin, key: String): String? { return dataMap.getString("${plugin.name.lowercase()}.$key") }

    fun getStringList(plugin: JavaPlugin, key: String): List<String> { return dataMap.getStringList("${plugin.name.lowercase()}.$key") }

    fun set(plugin: JavaPlugin, key: String, value: Any?) { dataMap.set("${plugin.name.lowercase()}.$key",value) }

    private val wallet = Wallet(
        getLong(main,"wallet.money"),
        getLong(main,"wallet.cash")
    )
    fun getPayment(type: PaymentType): Payment? = wallet.getPayment(type)

    private fun updateWallet() {
        PaymentType.values().forEach {
            set(main,"wallet.${it.name.lowercase()}", wallet.getPayment(it)?.balance)
        }
    }

    fun update() {
        updateWallet()
    }

    fun save() {
        dataMap.save(file)
    }

}