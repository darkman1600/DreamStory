package com.dreamstory.ability.extension

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import com.dreamstory.ability.manager.AbilityManager
import com.dreamstory.ability.manager.MysqlManager
import org.bukkit.entity.Player

fun Ability.updateSQL() {
    try {
        val s = MysqlManager.connection!!.prepareStatement(
            "UPDATE ability SET farm_lv=?,farm_exp=?,mine_lv=?,mine_exp=?,hunt_lv=?,hunt_exp=?,fish_lv=?,fish_exp=?,farm_skill=?,mine_skill=?,hunt_skill=?,fish_skill=? WHERE player=?"
        )
        s.setInt(1, getLevel(AbilityType.FARM))
        s.setLong(2, getExp(AbilityType.FARM))
        s.setInt(3, getLevel(AbilityType.MINE))
        s.setLong(4, getExp(AbilityType.MINE))
        s.setInt(5, getLevel(AbilityType.HUNT))
        s.setLong(6, getExp(AbilityType.HUNT))
        s.setInt(7, getLevel(AbilityType.FISH))
        s.setLong(8, getExp(AbilityType.FISH))
        s.setString(9, getSkillTree(AbilityType.FARM).toJson())
        s.setString(10, getSkillTree(AbilityType.MINE).toJson())
        s.setString(11, getSkillTree(AbilityType.HUNT).toJson())
        s.setString(12, getSkillTree(AbilityType.FISH).toJson())
        s.setInt(13, id)
        s.execute()
        s.close()
    } catch (e: Exception) {
        main.logger.info("§cid : $id 의 데이터 Ability 가 올바르게 저장되지 않았습니다.")
    }
}

fun Ability.registerSQL() {
    try {
        val s = MysqlManager.connection!!.prepareStatement("INSERT INTO ability (player,farm_lv,farm_exp,mine_lv,mine_exp,hunt_lv,hunt_exp,fish_lv,fish_exp,farm_skill,mine_skill,hunt_skill,fish_skill) values (?,?,?,?,?,?,?,?,?,?,?,?,?)")
        s.setInt(1, id);
        s.setInt(2, 0);
        s.setLong(3 , 0);
        s.setInt(4, 0);
        s.setLong(5 , 0);
        s.setInt(6, 0);
        s.setLong(7 , 0);
        s.setInt(8, 0);
        s.setLong(9 , 0);
        s.setString(10, this.getSkillTree(AbilityType.FARM).toJson());
        s.setString(11, this.getSkillTree(AbilityType.MINE).toJson());
        s.setString(12, this.getSkillTree(AbilityType.HUNT).toJson());
        s.setString(13, this.getSkillTree(AbilityType.FISH).toJson());
        s.execute();
        s.close();
    } catch (e: Exception) {
        main.logger.info("§cid : $id 의 데이터 Ability 가 올바르게 저장되지 않았습니다.")
    }
}


val Player.ability: Ability?
    get() = AbilityManager.getAbility(uniqueId)