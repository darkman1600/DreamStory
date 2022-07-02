package kr.dreamstory.ability.ability.play.command

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.SkillTree
import kr.dreamstory.ability.ability.play.region.RegionType
import kr.dreamstory.ability.ability.play.skills.EnumSkill
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

data class SkillCommand(
    private val p: Player,
    val inputType: InputType,
    private val text: String,
    private val regionType: RegionType? = null,
    var skillTree: SkillTree? = null,
    val enumSkill: EnumSkill? = null
) {

    private var current: Int = 100
    private var index: Int = 10
    var currentText: String = text
        private set
    private val owner: UUID = p.uniqueId

    fun register(): Boolean {
        val p = main.server.getPlayer(owner) ?: return false
        when(inputType) {
            InputType.INPUT-> {
                if(regionType == null) return false
                p.sendTitle("", currentText, 0, Int.MAX_VALUE, 0)
            }
            InputType.INSERT-> {
                if(skillTree == null || enumSkill == null) return false
                p.sendTitle("§f커맨드를 입력하세요.", currentText, 0, Int.MAX_VALUE, 0)
            }
        }
        p.playSound(p.location, Sound.BLOCK_LEVER_CLICK, 1f, 3f)
        return true
    }

    fun input(label: Int, text: String): Boolean {
        val p = main.server.getPlayer(owner)?: return false
        current+= label*index; index /= 10
        currentText += " $text"
        p.sendTitle(null, currentText, 0, Int.MAX_VALUE, 0)
        p.playSound(p.location, Sound.BLOCK_LEVER_CLICK, 1f, 3f)
        return index <= 0
    }

    fun getType(): RegionType = regionType ?: RegionType.NONE
    fun isEqualsType(regionType: RegionType? = null): Boolean = (this.regionType == null && inputType == InputType.INSERT) || regionType == this.regionType
    fun getFinal(): Int = current

}
