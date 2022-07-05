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

    fun getSkill(es: EnumSkill?): Skill? = if(es == null) null else try { skills[es.skillManagerIndex] } catch (e: Exception) { null }
    fun getDescription(type: AbilityType): ArrayList<String> { return arrayListOf("","") }

}