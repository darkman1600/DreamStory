package kr.dreamstory.ability.ability.play.skills

import kr.dreamstory.ability.ability.play.ability.AbilityType
import java.util.*
import java.util.stream.Collectors

enum class EnumSkill(val skillManagerIndex: Int, val skillTreeIndex: Int, val slot: Int, val slot2: Int, val conSkill: Boolean, val type: AbilityType) {

    MINEACTIVE(0,0, 10, 9,false, AbilityType.MINE),
    MINEBUFF(1,1,11,10,false, AbilityType.MINE),
    MINEPASSIVE(2,2, 12, 11,false, AbilityType.MINE),

    FARMACTIVE(3,0,10,9,false, AbilityType.FARM),
    FARMBUFF(4,1,11,10,false, AbilityType.FARM),
    FARMPASSIVE(5,2, 12, 11,false, AbilityType.FARM),

    FISHACTIVE(6,0,10,9,false, AbilityType.FISH),
    FISHBUFF(7,1,11,10,false, AbilityType.FISH),
    FISHPASSIVE(8,2, 12, 11,false, AbilityType.FISH),

    HUNTACTIVE(9,0,10,9,false, AbilityType.HUNT),
    HUNTBUFF(10,1,11,10,false, AbilityType.HUNT),
    HUNTPASSIVE(11,2, 12, 11,false, AbilityType.HUNT);


    fun getSlot(hidden: Boolean): Int = if(hidden) slot2 else slot

    companion object {
        fun toTreeIndexEnumSkill(index: Int,conSkill: Boolean): EnumSkill? = Arrays.stream(values()).filter{ it.skillTreeIndex == index && it.conSkill == conSkill }.collect(Collectors.toList())?.get(0)
        fun toSkillManagerIndexOf(index: Int): EnumSkill = values().filter { it.skillManagerIndex == index }[0]
        fun getSkillList(abilityType: AbilityType): List<EnumSkill> =Arrays.stream(EnumSkill.values()).filter { it.type == abilityType }.collect(Collectors.toList())

    }

}