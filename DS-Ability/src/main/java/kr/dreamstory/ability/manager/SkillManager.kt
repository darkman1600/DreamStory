package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.skills.Active
import kr.dreamstory.ability.ability.play.skills.EnumSkill
import kr.dreamstory.ability.ability.play.skills.Skill
import kr.dreamstory.ability.ability.play.skills.farm.FarmActive
import kr.dreamstory.ability.ability.play.skills.farm.FarmBuff
import kr.dreamstory.ability.ability.play.skills.farm.FarmPassive
import kr.dreamstory.ability.ability.play.skills.fish.FishActive
import kr.dreamstory.ability.ability.play.skills.fish.FishBuff
import kr.dreamstory.ability.ability.play.skills.fish.FishPassive
import kr.dreamstory.ability.ability.play.skills.hunt.HuntActive
import kr.dreamstory.ability.ability.play.skills.hunt.HuntBuff
import kr.dreamstory.ability.ability.play.skills.hunt.HuntPassive
import kr.dreamstory.ability.ability.play.skills.mine.MineActive
import kr.dreamstory.ability.ability.play.skills.mine.MineBuff
import kr.dreamstory.ability.ability.play.skills.mine.MinePassive
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.extension.toJson
import org.bukkit.Material
import org.bukkit.entity.Player

object SkillManager {

    private val skills: Array<Skill?> by lazy { arrayOf(
        MineActive(),
        MineBuff(),
        MinePassive(),
        FarmActive(),
        FarmBuff(),
        FarmPassive(),
        FishActive(),
        FishBuff(),
        FishPassive(),
        HuntActive(),
        HuntBuff(),
        HuntPassive()
    ) }

    fun coolTimeSave(p: Player) {
        val coolMap = HashMap<Material, Int>()
        skills.filter { it is Active }.forEach {
            with(it as Active) {
                if(p.hasCooldown(getCoolTimeIcon())) coolMap[getCoolTimeIcon()] = p.getCooldown(getCoolTimeIcon())
            }
        }
        val check = MysqlManager.executeQuery<Int?>("cool_times","player","player",p.id) != null
        if(coolMap.isNotEmpty()) {
            if(check) MysqlManager.executeQuery("update cool_times set cool_times='${coolMap.toJson()}' where player=${p.id}")
            else MysqlManager.executeQuery("insert into cool_times(player, cool_times) values (${p.id},'${coolMap.toJson()}')")
        } else if(check) MysqlManager.executeQuery("delete from cool_times where player=${p.id}")
    }

    fun getSkill(es: EnumSkill?): Skill? = if(es == null) null else try { skills[es.skillManagerIndex] } catch (e: Exception) { null }
    fun getDescription(type: AbilityType): ArrayList<String> { return arrayListOf("","") }

}