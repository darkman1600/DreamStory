package com.dreamstory.ability.manager

import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object LogManager {

    private val baseFolder by lazy {
        val file = File("C:/log")
        if(!file.exists()) file.mkdir()
        file
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd-kk-mm-ss")
    private val Date.fileFormat get() = dateFormat.format(this)

    fun saveLogAll(server: Server) { server.onlinePlayers.forEach { saveLog(it, LogType.AUTO) } }
    fun saveLog(p: Player, type: LogType) { if(p.isOnline) saveLog(p.uniqueId, ItemListManager.toBase64(p.inventory), type) }

    fun saveLog(uuid: UUID, inv: String, type: LogType) {
        val folder = File(baseFolder, uuid.toString())
        if(!folder.exists()) folder.mkdirs()
        val date = Date()
        val fileName = date.fileFormat

        val saveFolder = File(folder, type.toString().toLowerCase())
        if(!saveFolder.exists()) saveFolder.mkdirs()

        var file = File(saveFolder, "$fileName.log")
        var i = 1
        while (file.exists()) {
            file = File(saveFolder, "$fileName($i).log")
            if (i > 50) return
            i++
        }
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config["data"] = inv
        config.save(file)
    }

    fun containsLogFile(uuid: UUID): Boolean {
        val folder = File(baseFolder, uuid.toString())
        return folder.exists()
    }

    fun getLogFiles(uuid: UUID, type: LogType): Array<File>? {
        val folder = File(baseFolder, uuid.toString())
        if(!folder.exists()) return null
        val saveFolder = File(folder, type.toString().toLowerCase())
        if(!saveFolder.exists()) return null
        return saveFolder.listFiles()
    }

    fun openInventory(file: File): Array<ItemStack> {
        val config = YamlConfiguration.loadConfiguration(file)
        return ItemListManager.itemStackArrayFromBase64(config.getString("data")!!)
    }

    enum class LogType { JOIN,QUIT,AUTO }
}