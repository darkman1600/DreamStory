package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.data.WeatherType
import kr.dreamstory.ability.ability.play.region.Region
import kr.dreamstory.ability.ability.play.region.RegionType
import kr.dreamstory.ability.extension.locationToStringData
import kr.dreamstory.ability.extension.parseLocation
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.systemTime
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object RegionManager {

    @Deprecated(message = "실행하지 마세요",level = DeprecationLevel.WARNING)
    fun loadDataTask() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(100)
            repeating(1200)
            while(true) {
                val file = File("${main.dataFolder.path}\\database","regions.yml")
                val config = YamlConfiguration.loadConfiguration(file)
                config.getKeys(false).forEach {
                    load(it)
                }
                yield()
            }
        }
    }

    private fun load(key: String) {
        val file = File("${main.dataFolder.path}\\database","regions.yml")
        if(!file.exists()) return
        val config = YamlConfiguration.loadConfiguration(file)
        val protectName = config.getString("$key.wg_name")!!
        val name = config.getString("$key.name")!!
        if(regionMap.containsKey(key)) return
        else main.logger.info("§e새로운 지역이 추가되어 서버에 적용됩니다. §7[ $name - $key ]")
        val des = config.getString("$key.des")!!
        val spawn = config.getString("$key.spawn")!!.parseLocation()!!
        val weather = WeatherType.indexOf(config.getInt("$key.weather"))
        val regionType = RegionType.indexOf(config.getInt("$key.type"))
        val lastUpdate = config.getLong("$key.last_update")

        regionMap[key] = Region(key, protectName, spawn, name, lastUpdate, des, weather,regionType)
    }

    private val regionMap = HashMap<String, Region>()
    private val regionPlayers = HashMap<UUID, String>()

    val regionList: List<String>
        get() = regionMap.keys.toList()
    fun getPlayerInRegion(uuid: UUID): Region? {
        val key = regionPlayers[uuid]?: return null
        return regionMap[key]
    }
    fun getRegionPlayers(region: Region): List<UUID> {
        val name = region.protectName
        return regionPlayers.filter{ it.value == name }.keys.toList()
    }

    fun removeRegionToPlayer(uuid: UUID, region: Region) { regionPlayers.remove(uuid) }
    fun addRegionToPlayer(uuid: UUID,region: Region) { regionPlayers[uuid] = region.protectName }
    fun getRegion(protectName: String): Region? = regionMap[protectName]

    @Deprecated(message = "쿼리에는 저장되지 않습니다.", level = DeprecationLevel.WARNING)
    fun unregisterRegion(protectName: String) {
        regionMap.remove(protectName)
        regionPlayers.filter { it.value == protectName }.keys.forEach { regionPlayers.remove(it) }
    }

    fun unregisterRegion(region: Region) {
        region.saved = false
        unregisterRegion(region.protectName)
        val file = File("${main.dataFolder.path}\\database","regions.yml")
        val config = YamlConfiguration.loadConfiguration(file)
        config.set(region.protectName,null)
        config.save(file)
    }

    fun registerRegion(protectName: String, name: String, p: Player): Boolean {
        if(regionMap.containsKey(protectName)) return false
        val loc = p.location
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            val file = File("${main.dataFolder.path}\\database","regions.yml")
            val c = YamlConfiguration.loadConfiguration(file)
            c.set("$protectName.wg_name",protectName)
            c.set("$protectName.name",name)
            c.set("$protectName.spawn", locationToStringData(loc))
            c.set("$protectName.last_update", systemTime)
            regionMap[protectName] = Region(protectName, protectName, loc, name, systemTime)
        }
        return true
    }
}