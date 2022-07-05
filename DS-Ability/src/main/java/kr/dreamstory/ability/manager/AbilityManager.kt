package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import java.util.*

object AbilityManager {

    val abilityMap = HashMap<UUID, Ability>()

    fun removeData(uuid: UUID) { abilityMap.remove(uuid) }
    fun getAbility(uuid: UUID): Ability = abilityMap[uuid] ?: getNewAbility(uuid)
    fun getNewAbility(uuid: UUID): Ability {
        val d = PlayerDataManger.getPlayerData(uuid)
        val ability = Ability(
            uuid,
            d.getInt(main,"ability.mine.level"),
            d.getLong(main,"ability.mine.exp"),
            d.getInt(main,"ability.farm.level"),
            d.getLong(main,"ability.farm.exp"),
            d.getInt(main,"ability.fish.level"),
            d.getLong(main,"ability.fish.exp"),
            d.getInt(main,"ability.hunt.level"),
            d.getLong(main,"ability.hunt.exp"),
            d.getString(main,"ability.mine.skill"),
            d.getString(main,"ability.farm.skill"),
            d.getString(main,"ability.fish.skill"),
            d.getString(main,"ability.hunt.skill")
        )
        abilityMap[uuid] = ability
        return abilityMap[uuid]!!
    }

    fun saveAndQuit(uuid: UUID) {
        abilityMap.remove(uuid)?.updateData()
    }

    fun saveAll() {
        main.server.scheduler.schedule(main,SynchronizationContext.ASYNC) {
            abilityMap.values.forEach { it.updateData() }
        }
    }
}