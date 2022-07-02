package com.dreamstory.ability.extension

import kr.dreamstory.ability.ability.play.island.DSIsland
import com.dreamstory.ability.manager.DSIslandManager
import org.bukkit.entity.Player

val Player.island: DSIsland?
    get() = DSIslandManager.getDSIsland(id, true)