package com.dreamstory.ability.extension

import kr.dreamstory.ability.ability.play.region.Region
import com.dreamstory.ability.manager.RegionManager
import org.bukkit.entity.Player

val Player.region: Region?
    get() = RegionManager.getPlayerInRegion(uniqueId)
