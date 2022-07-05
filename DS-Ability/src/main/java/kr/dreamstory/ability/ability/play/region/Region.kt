package kr.dreamstory.ability.ability.play.region

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.data.WeatherType
import kr.dreamstory.ability.extension.locationToStringData
import kr.dreamstory.ability.extension.parseLocation
import kr.dreamstory.ability.manager.RegionManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class Region(
    val key: String,
    val protectName: String,
    var spawn: Location,
    var name: String,
    var lastUpdate: Long,
    var des: String = "",
    var weatherType: WeatherType = WeatherType.SUN,
    var regionType: RegionType = RegionType.NONE,
    val clone: Boolean = false,
) {

    var saved: Boolean = true
    fun clone(): Region = Region(key, protectName, spawn, name, lastUpdate, des, weatherType, regionType, true)

    init {
        if(!clone) taskStart()
    }

    val players: List<UUID>
        get() = RegionManager.getRegionPlayers(this)

    fun update() {
        val date = Date()
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            val file = File("${main.dataFolder.path}\\database","regions.yml")
            val config = YamlConfiguration.loadConfiguration(file)
            config.set("$protectName.spawn", locationToStringData(spawn))
            config.set("$protectName.name",name)
            config.set("$protectName.last_update",date.time)
            config.set("$protectName.des",des)
            config.set("$protectName.weather",weatherType.index)
            config.save(file)
        }
    }

    private fun taskStart() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            repeating(100)
            while(true) {
                if(!saved) break
                val file = File("${main.dataFolder.path}\\database","regions.yml")
                val config = YamlConfiguration.loadConfiguration(file)
                val date = config.getLong("$protectName.last_update")
                if(date > lastUpdate) {
                    main.logger.info("§e지역의 데이터 변화가 감지되어 서버에 적용됩니다 .§7[ $name - $protectName ]")
                    // update
                    lastUpdate = date
                    name = config.getString("$protectName.name")!!
                    des = config.getString("$protectName.des")!!
                    spawn = config.getString("$protectName.spawn")!!.parseLocation()!!
                    weatherType = WeatherType.indexOf(config.getInt("$protectName.weather"))
                    regionType = RegionType.indexOf(config.getInt("$protectName.type"))
                }
                yield()
            }
        }
    }

    fun equals(o: Region): Boolean {
        /*println("key: $key / ${o.key}")
        println("name: $name / ${o.name}")
        println("des: $des / ${o.des}")
        println("protectName: $protectName / ${o.protectName}")
        println("spawn: ${spawn} / ${o.spawn}")
        println("weatherType: $weatherType / ${o.weatherType}")
        println("regionType: $regionType / ${o.regionType}")*/
        return key == o.key && name == o.name && protectName == o.protectName
                && des == o.des && spawn.toString() == o.spawn.toString() && weatherType == o.weatherType
                && regionType == o.regionType
    }

}