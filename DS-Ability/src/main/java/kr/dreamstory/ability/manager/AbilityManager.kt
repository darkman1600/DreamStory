package kr.dreamstory.ability.manager

import kotlinx.coroutines.*
import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.data.PlayerDataManger
import java.util.*

object AbilityManager {

    private val abilityMap = HashMap<UUID, Ability>()

    fun getAbility(uuid: UUID): Ability? = abilityMap[uuid]
    fun loadAbility(d: PlayerData): Boolean {
        val uuid = d.uuid
        val ability = Ability(
            uuid,
            d.getInt("ability.mine.level"),
            d.getLong("ability.mine.exp"),
            d.getInt("ability.farm.level"),
            d.getLong("ability.farm.exp"),
            d.getInt("ability.fish.level"),
            d.getLong("ability.fish.exp") ,
            d.getInt("ability.hunt.level"),
            d.getLong("ability.hunt.exp"),
            d.getStringOrNull("ability.mine.skill"),
            d.getStringOrNull("ability.farm.skill"),
            d.getStringOrNull("ability.fish.skill"),
            d.getStringOrNull("ability.hunt.skill"),
            d.getBoolean("ability.actionbar.enable",true)
        )
        abilityMap[uuid] = ability
        return true
    }

    fun saveAndQuit(uuid: UUID) {
        abilityMap.remove(uuid)?.updateData()
    }

    fun updateAll() {
        main.server.scheduler.schedule(main,SynchronizationContext.ASYNC) {
            abilityMap.values.forEach { it.updateData() }
        }
    }

}