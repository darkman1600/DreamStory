package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.data.PlayerDataManger
import java.util.*

object AbilityManager {

    val abilityMap = HashMap<UUID, Ability>()

    fun removeData(uuid: UUID) { abilityMap.remove(uuid) }
    fun getAbility(uuid: UUID): Ability = abilityMap[uuid]!!
    fun loadAbility(uuid: UUID): Ability? {
        val d = PlayerDataManger.getPlayerData(uuid) ?: return null
        val ability = Ability(
            uuid,
            d.getInt(main,"ability.mine.level"),
            d.getLong(main,"ability.mine.exp"),
            d.getInt(main,"ability.farm.level"),
            d.getLong(main,"ability.farm.exp"),
            d.getInt(main,"ability.fish.level"),
            d.getLong(main,"ability.fish.exp") ,
            d.getInt(main,"ability.hunt.level"),
            d.getLong(main,"ability.hunt.exp"),
            d.getStringOrNull(main,"ability.mine.skill"),
            d.getStringOrNull(main,"ability.farm.skill"),
            d.getStringOrNull(main,"ability.fish.skill"),
            d.getStringOrNull(main,"ability.hunt.skill")
        )
        abilityMap[uuid] = ability
        return abilityMap[uuid]
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