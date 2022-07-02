package kr.dreamstory.ability.ability.play.ability

import kr.dreamstory.ability.ability.play.skills.EnumSkill
import kr.dreamstory.ability.ability.play.skills.Skill
import com.dreamstory.ability.manager.SkillManager

data class SkillTree(
    var point: Int = 0,
    private var resetCount: Int = 0,
    private var hiddenSkillIndex: Short = -1,
    private var hiddenSkill:Int = 0
) {

    private var jobSkills:IntArray = IntArray(3)
    private var conSkills:IntArray = IntArray(3)
    private var commands = HashMap<Int, EnumSkill>()
    val hasLearnHiddenSkill: Boolean
        get() = hiddenSkillIndex.toInt() != -1

    fun getHiddenSkillIndex(): Short = hiddenSkillIndex
    fun setHiddenSkillIndex(index: Short) { hiddenSkillIndex = index }
    fun getResetCount(): Int = resetCount

    fun getSkillLevel(skillManagerIndex: Int): Int = getSkillLevel(EnumSkill.toSkillManagerIndexOf(skillManagerIndex))
    fun getHiddenSkillLevel(): Int = hiddenSkillIndex.toInt()
    fun addHiddenSkillLevel(): Int = ++hiddenSkill
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
        if(es.conSkill) return conSkills[es.skillTreeIndex]
        else return jobSkills[es.skillTreeIndex]
    }

    fun addSkillLevel(es: EnumSkill) {
        if(!es.conSkill) {
            point--
            jobSkills[es.skillTreeIndex]++
        } else {

        }
    }

    fun resetSkillLevel(es: EnumSkill) {
        if(!es.conSkill) {
            jobSkills[es.skillTreeIndex] = 0
        } else {
            conSkills[es.skillTreeIndex] = 0
        }
    }

    fun getLearnSkillsAtEnumSkillList(): ArrayList<EnumSkill> {
        val list = ArrayList<EnumSkill>()
        for(i in 0 until 3) {
            if(jobSkills[i] > 0) {
                val es = EnumSkill.toTreeIndexEnumSkill(i,false)
                if(es != null) list.add(es)
            }
            if(conSkills[i] > 0) {
                val es = EnumSkill.toTreeIndexEnumSkill(i,true)
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
