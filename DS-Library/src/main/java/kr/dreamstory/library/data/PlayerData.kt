package kr.dreamstory.library.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.economy.Payment
import kr.dreamstory.library.economy.PaymentType
import kr.dreamstory.library.economy.Wallet
import kr.dreamstory.library.extension.toBase64
import kr.dreamstory.library.item.minecraft.toItemStackFromBase64
import kr.dreamstory.library.main
import kr.dreamstory.library.permission.PermissionGrade
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class PlayerData(val uuid: UUID) {

    private val file = File("${main.dataFolder.path}\\database\\player_data","$uuid")
    private val dataMap = YamlConfiguration.loadConfiguration(file)

    val hasPlayedBefore = file.exists()
    val name: String? = if(hasPlayedBefore) getStringOrNull(main,"name") else null
    fun setName(str: String?) {
        set(main,"name",str,true)
    }
    val head: ItemStack? = if(hasPlayedBefore) {
        getStringOrNull(main,"head")?.toItemStackFromBase64()
    } else {
        null
    }
    fun setHead(itemStack: ItemStack) {
        set(main,"head",itemStack.toBase64(),true)
    }

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

    fun set(plugin: JavaPlugin, key: String, value: Any?, save: Boolean = false) {
        dataMap.set("${plugin.name.lowercase()}.$key",value)
        if(save) asyncSave()
    }

    fun addToStringList(plugin: JavaPlugin, key: String, str: String, save: Boolean = false): Boolean {
        val list = getStringList(plugin,key)
        if(list.contains(str)) return false
        set(plugin,key,list + str)
        if(save) asyncSave()
        return true
    }







    var permission = PermissionGrade.fromString((getStringOrNull(main,"permission") ?: "NEWBIE").toUpperCase())!!
        private set

    fun setPermission(grade: PermissionGrade) { permission = grade }

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

    fun asyncSave() {
        main.schedule(SynchronizationContext.ASYNC) {
            dataMap.save(file)
        }
    }

}