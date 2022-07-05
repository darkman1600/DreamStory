package kr.dreamstory.ability.ability.play.ability

import kr.dreamstory.ability.ability.play.skills.EnumSkill
import kr.dreamstory.ability.ability.play.skills.Skill
import com.dreamstory.ability.manager.SkillManager

data class SkillTree(
    var point: Int = 0,
    private var resetCount: Int = 0,
) {

    private var jobSkills:IntArray = IntArray(3)
    private var commands = HashMap<Int, EnumSkill>()

    fun getResetCount(): Int = resetCount

    fun getSkillLevel(skillManagerIndex: Int): Int = getSkillLevel(EnumSkill.fromSkillManagerIndex(skillManagerIndex))
    fun getCommand(es: EnumSkill): Int {
        var result = 0
        commands.forEach { (key, enumSkill) ->
            if(enumSkill == es) {
                result = key
                return@forEach
            }
        }
        return result
    }

    fun getSkill(command: Int): Skill? = SkillManager.getSkill(commands[command])

    fun setCommand(command: Int, skill: EnumSkill? = null): Boolean {
        if(skill == null) return false
        val current = getCommand(skill)
        if (current != 0) commands.remove(current)
        if (commands.containsKey(command)) {
            return false
        }
        commands[command] = skill
        return true
    }

    fun getSkillLevel(es: EnumSkill?): Int {
        if(es == null) return 0
        return jobSkills[es.skillTreeIndex]
    }

    fun addSkillLevel(es: EnumSkill) {
        point--
        jobSkills[es.skillTreeIndex]++
    }

    fun resetSkillLevel(es: EnumSkill) {
        jobSkills[es.skillTreeIndex] = 0
    }

    fun getLearnSkillsAtEnumSkillList(): ArrayList<EnumSkill> {
        val list = ArrayList<EnumSkill>()
        for(i in 0 until 3) {
            if(jobSkills[i] > 0) {
                val es = EnumSkill.fromSkillTreeIndex(i)
                if(es != null) list.add(es)
            }
        }
        return list
    }

    fun reset(countReset: Boolean): Int {
        var count = 0
        EnumSkill.values().forEach { count += getSkillLevel(it); resetSkillLevel(it)  }
        if(countReset) resetCount = 0
        else resetCount++
        return count
    }

}
