package kr.dreamstory.library.data

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.economy.Payment
import kr.dreamstory.library.economy.PaymentType
import kr.dreamstory.library.economy.Wallet
import kr.dreamstory.library.extension.toBase64
import kr.dreamstory.library.extension.toItemStackFromBase64
import kr.dreamstory.library.main
import kr.dreamstory.library.permission.PermissionGrade
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.File
import java.util.UUID

class PlayerData(val uuid: UUID) {

    private val file = File("${main.dataFolder.path}\\database\\player_data","$uuid")
    private val dataMap = YamlConfiguration.loadConfiguration(file)

    val hasPlayedBefore = file.exists()
    var name: String? = getStringOrNull("name")
        private set
    var head: ItemStack? = getStringOrNull("head")?.toItemStackFromBase64()
        private set

    fun joinUpdate(player: Player) {
        name = player.name
        if(!hasPlayedBefore) {
            val hb = ItemStack(Material.PLAYER_HEAD)
            val hm = hb.itemMeta as SkullMeta
            hm.owningPlayer = player
            hb.itemMeta = hm
            head = hb
            set("head",head!!.toBase64())
        }
        set("name",name)
    }

    fun getInt(key: String, default: Int = 0) = dataMap.getInt(key, default)
    fun getIntOrNull(key: String): Int? {
        val str = dataMap.getString(key) ?: return null
        return str.toIntOrNull()
    }
    fun getLong(key: String, default: Long = 0): Long = dataMap.getLong(key,default)
    fun getLongOrNull(key: String): Long? {
        val str = dataMap.getString(key) ?: return null
        return str.toLongOrNull()
    }
    fun getStringOrNull(key: String): String? { return dataMap.getString(key) }
    fun getStringList(key: String): List<String> { return dataMap.getStringList(key) }
    fun getBoolean(key: String, default: Boolean = false): Boolean = dataMap.getBoolean(key,default)

    fun set(key: String, value: Any?, save: Boolean = false) {
        dataMap.set(key,value)
        if(save) asyncSave()
    }

    fun addToStringList(key: String, str: String, save: Boolean = false): Boolean {
        val list = getStringList(key)
        if(list.contains(str)) return false
        set(key,list + str)
        if(save) asyncSave()
        return true
    }







    var permission = PermissionGrade.fromString((getStringOrNull("permission") ?: "NEWBIE").toUpperCase())!!
        private set

    fun setPermission(grade: PermissionGrade) { permission = grade }

    private val wallet = Wallet(
        getLong("wallet.money"),
        getLong("wallet.cash")
    )

    fun getPayment(type: PaymentType): Payment? = wallet.getPayment(type)

    private fun updateWallet() {
        PaymentType.values().forEach {
            set("wallet.${it.name.lowercase()}", wallet.getPayment(it)?.balance)
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