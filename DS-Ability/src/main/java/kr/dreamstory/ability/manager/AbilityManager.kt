package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.extension.registerSQL
import com.dreamstory.ability.extension.updateSQL
import java.util.*

object AbilityManager {

    val abilityMap = HashMap<Int, Ability>()

    fun inputData(id: Int, ab: Ability) { abilityMap[id] = ab }
    fun removeData(id: Int) { abilityMap.remove(id) }
    fun getAbility(id: Int): Ability? = abilityMap[id]?: getNewAbility(id,true)
    fun getTempAbility(id: Int): Ability? {
        if(id <= 0) return null
        return abilityMap[id]?: getNewAbility(id, false)
    }
    fun getAbility(uuid: UUID): Ability? {
        val id = DSCoreAPI.getPlayerId(uuid)
        return if(id <= 0) null
        else abilityMap[id]?: getNewAbility(id,true)
    }

    fun saveAll() { abilityMap.values.forEach { it.updateSQL() } }

    private fun getNewAbility(id: Int, save: Boolean): Ability? {
        var ab: Ability? = null
        try {
            val sql = "SELECT * FROM ability WHERE player=?"
            val s = MysqlManager.connection!!.prepareStatement(sql)
            s.setInt(1, id)
            val set = s.executeQuery()
            if (set.next()) {
                ab = Ability(
                    id,
                    set.getInt("farm_lv"),
                    set.getLong("farm_exp"),
                    set.getInt("mine_lv"),
                    set.getLong("mine_exp"),
                    set.getInt("hunt_lv"),
                    set.getLong("hunt_exp"),
                    set.getInt("fish_lv"),
                    set.getLong("fish_exp"),
                    set.getString("farm_skill"),
                    set.getString("mine_skill"),
                    set.getString("hunt_skill"),
                    set.getString("fish_skill"),
                    save
                )
            } else {
                if (save) {
                    ab = Ability(id)
                    ab.registerSQL()
                }
            }
            s.close()
            set.close()
        } catch (e: Exception) {
            e.printStackTrace()
            main.logger.info("§cid : $id 의 유저 Ability 정보 로드에 실패 하였습니다.")
        }
        return ab
    }

}