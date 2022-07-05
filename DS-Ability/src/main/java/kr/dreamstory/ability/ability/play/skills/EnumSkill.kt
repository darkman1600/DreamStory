package kr.dreamstory.ability.ability.play.skills

import kr.dreamstory.ability.ability.play.ability.AbilityType

enum class EnumSkill(val skillManagerIndex: Int, val skillTreeIndex: Int, val slot: Int, val type: AbilityType) {

    MINEACTIVE(0,0, 10, AbilityType.MINE),
    MINEBUFF(1,1,11, AbilityType.MINE),
    MINEPASSIVE(2,2, 12, AbilityType.MINE),

    FARMACTIVE(3,0,10, AbilityType.FARM),
    FARMBUFF(4,1,11, AbilityType.FARM),
    FARMPASSIVE(5,2, 12, AbilityType.FARM),

    FISHACTIVE(6,0,10, AbilityType.FISH),
    FISHBUFF(7,1,11, AbilityType.FISH),
    FISHPASSIVE(8,2, 12, AbilityType.FISH),

    HUNTACTIVE(9,0,10, AbilityType.HUNT),
    HUNTBUFF(10,1,11, AbilityType.HUNT),
    HUNTPASSIVE(11,2, 12, AbilityType.HUNT);


    companion object {
        fun fromSkillTreeIndex(index: Int): EnumSkill? = values().firstOrNull { it.skillTreeIndex == index }
        fun fromSkillManagerIndex(index: Int): EnumSkill = values().first { it.skillManagerIndex == index }
        fun getSkillList(abilityType: AbilityType): List<EnumSkill> = values().filter { it.type == abilityType }

    }

}