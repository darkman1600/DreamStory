package kr.dreamstory.ability.extension

import kr.dreamstory.ability.ability.play.region.Region
import kr.dreamstory.ability.manager.RegionManager
import org.bukkit.entity.Player

val Player.region: Region?
    get() = RegionManager.getPlayerInRegion(uniqueId)
