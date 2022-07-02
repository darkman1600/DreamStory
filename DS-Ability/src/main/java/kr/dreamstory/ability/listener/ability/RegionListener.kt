package com.dreamstory.ability.listener.ability

import kr.dreamstory.ability.ability.event.RegionEnterEvent
import kr.dreamstory.ability.ability.event.RegionLeaveEvent
import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.data.WeatherType
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.java.worldguard.events.ProtectRegionEnterEvent
import com.dreamstory.ability.java.worldguard.events.ProtectRegionLeaveEvent
import com.dreamstory.ability.listener.interfaces.ChannelListener
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.manager.RegionManager
import org.bukkit.event.EventHandler

class RegionListener: ChannelListener {

    @EventHandler
    fun regionEnter(e: ProtectRegionEnterEvent) {
        val name = e.region.id
        val region = RegionManager.getRegion(name)?: return

        val p = e.player
        RegionManager.addRegionToPlayer(p.uniqueId, region)
        MysqlManager.executeQuery("UPDATE player SET region=${region.key} WHERE id=${p.id}")
        p.sendTitle(region.name, region.des, 10 , 40, 20)
        if(region.weatherType == WeatherType.SUN) p.setPlayerWeather(org.bukkit.WeatherType.CLEAR)
        else p.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL)
        main.server.pluginManager.callEvent(RegionEnterEvent(p, region))
    }

    @EventHandler
    fun regionLeave(e: ProtectRegionLeaveEvent) {
        val p = e.player
        val name = e.region.id
        if(p.playerWeather?.equals(org.bukkit.WeatherType.DOWNFALL) == true) { p.setPlayerWeather(org.bukkit.WeatherType.CLEAR) }
        val region = RegionManager.getRegion(name)?: return

        MysqlManager.executeQuery("UPDATE player SET region=0 WHERE id=${p.id}")
        RegionManager.removeRegionToPlayer(p.uniqueId, region)
        main.server.pluginManager.callEvent(RegionLeaveEvent(p, region))
    }

}