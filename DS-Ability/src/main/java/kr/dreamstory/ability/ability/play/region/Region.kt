package kr.dreamstory.ability.ability.play.region

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.data.WeatherType
import com.dreamstory.ability.extension.locationToDSLocationString
import com.dreamstory.ability.extension.parseDSLocation
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.manager.RegionManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.Location
import java.util.*

class Region(
    val key: Int,
    val protectName: String,
    var spawn: Location,
    var name: String,
    var lastUpdate: Date,
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
            MysqlManager.executeQuery("UPDATE region SET spawn='${locationToDSLocationString(spawn)}', " +
                    "name='$name', last_update=${date.time}, des='$des', " +
                    "weather=${weatherType.index}, type=${regionType.index} WHERE id=$key")
        }
    }

    private fun taskStart() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            repeating(100)
            while(true) {
                if(!saved) break
                val map = MysqlManager.executeQuery("region","id",key)
                if(map.isEmpty()) {
                    RegionManager.unregisterRegion(protectName)
                    saved = false
                    break
                }
                val date = (map["last_update"] as String).toLong()
                if(date > lastUpdate.time) {
                    main.logger.info("§e지역의 데이터 변화가 감지되어 서버에 적용됩니다 .§7[ $name - $protectName ]")
                    // update
                    lastUpdate = Date(date)
                    name = map["name"] as String
                    des = map["des"] as String
                    spawn = (map["spawn"] as String).parseDSLocation()!!.location!!
                    weatherType = WeatherType.indexOf(map["weather"] as Int)
                    regionType = RegionType.indexOf(map["type"] as Int)
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