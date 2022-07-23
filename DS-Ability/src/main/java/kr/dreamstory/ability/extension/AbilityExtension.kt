package kr.dreamstory.ability.extension

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.manager.AbilityManager
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.extension.toJson
import org.bukkit.entity.Player

val Player.ability: Ability?
    get() = AbilityManager.getAbility(uniqueId)