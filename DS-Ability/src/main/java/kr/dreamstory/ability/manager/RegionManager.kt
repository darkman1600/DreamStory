package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.data.WeatherType
import kr.dreamstory.ability.ability.play.region.Region
import kr.dreamstory.ability.ability.play.region.RegionType
import com.dreamstory.ability.extension.locationToDSLocationString
import com.dreamstory.ability.extension.parseDSLocation
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
import org.bukkit.entity.Player
import java.util.*

object RegionManager {

    @Deprecated(message = "실행하지 마세요",level = DeprecationLevel.WARNING)
    fun loadDataTask() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(100)
            repeating(1200)
            while(true) {
                val s = MysqlManager.connection!!.prepareStatement("SELECT id FROM region WHERE 1")
                val set = s.executeQuery()
                while(set.next()) {
                    val id = set.getInt("id")
                    try {
                        load(id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        main.logger.info("region data error : id = $id")
                    }
                }
                set.close()
                s.close()
                yield()
            }
        }
    }

    private fun load(id: Int) {
        val resultMap = MysqlManager.executeQuery("region","id", id)
        if(resultMap.isEmpty()) return
        val name = resultMap["name"] as String
        val protectName = resultMap["wg_name"] as String
        if(regionMap.containsKey(protectName)) return
        else main.logger.info("§e새로운 지역이 추가되어 서버에 적용됩니다. §7[ $name - $protectName ]")
        val des = resultMap["des"] as String
        val spawn = (resultMap["spawn"] as String).parseDSLocation()!!.location!!
        val weather = WeatherType.indexOf(resultMap["weather"] as Int)
        val regionType = RegionType.indexOf(resultMap["type"] as Int)
        val lastUpdate = resultMap["last_update"] as String
        val date = Date(lastUpdate.toLong())

        regionMap[protectName] = Region(id, protectName, spawn, name, date, des, weather,regionType)
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

    @Deprecated(message = "쿼리에 저장됩니다.",level= DeprecationLevel.WARNING)
    fun unregisterRegion(region: Region) {
        region.saved = false
        unregisterRegion(region.protectName)
        MysqlManager.executeQuery("DELETE FROM region WHERE id=${region.key}")
    }

    fun registerRegion(protectName: String, name: String,p: Player): Boolean {
        if(regionMap.filter { it.value.protectName == protectName }.isNotEmpty()) return false
        val date = Date()
        val time = date.time
        val loc = p.location
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            MysqlManager.executeQuery(
                "INSERT INTO region (wg_name, name, spawn, last_update) values" +
                        "('$protectName', '$name', '${locationToDSLocationString(loc)}', $time)")
            waitFor(1)
            val id: Int? = MysqlManager.executeQuery("region","id","wg_name",protectName)
            regionMap[protectName] = Region(id!!, protectName, loc,name, date)
        }
        return true
    }

    fun getRegionId(protectName: String): Int = MysqlManager.executeQuery("region", "id", "wg_name", protectName) ?: 0


}