package kr.dreamstory.ability.ability.play.ability

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.hud.HUDSkin
import kr.dreamstory.ability.manager.ActionBarManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.extension.fromJson
import kr.dreamstory.library.extension.toJson
import org.bukkit.Material
import java.util.UUID
import kotlin.math.roundToInt

data class Ability(val uuid: UUID) {

    private var levels: Array<Int> = Array(4) { 0 }
    private var exps: Array<Long> = Array(4) { 0 }
    private lateinit var skills: Array<SkillTree>
    lateinit var actionBar: String
        private set
    lateinit var expBar: String
        private set
    lateinit var levelBar: String
        private set
    private var actionBarUpdate: Boolean = false

    constructor(
            uuid: UUID,
            mineLv: Int, mineExp: Long,
            farmLv: Int, farmExp: Long,
            fishLv: Int, fishExp: Long,
            huntLv: Int, huntExp: Long,
            mineSkill: String?, farmSkill: String?,
            fishSkill: String?, huntSkill: String?
    ): this(uuid) {
        levels[AbilityType.MINE.index] = mineLv
        exps[AbilityType.MINE.index] = mineExp

        levels[AbilityType.FARM.index] = farmLv
        exps[AbilityType.FARM.index] = farmExp

        levels[AbilityType.FISH.index] = fishLv
        exps[AbilityType.FISH.index] = fishExp

        levels[AbilityType.HUNT.index] = huntLv
        exps[AbilityType.HUNT.index] = huntExp

        skills = arrayOf(
            mineSkill?.fromJson() ?: SkillTree(),
            farmSkill?.fromJson() ?: SkillTree(),
            fishSkill?.fromJson() ?: SkillTree(),
            huntSkill?.fromJson() ?: SkillTree()
        )
        actionBar = parseActionBar()
    }

    fun getLevel(type: AbilityType): Int = levels[type.index]
    fun getExp(type: AbilityType): Long = exps[type.index]
    fun getMaxExp(type: AbilityType): Long = MaxExps.getMaxExp(levels[type.index])
    fun getSkillTree(type: AbilityType): SkillTree = skills[type.index]
    fun getSkillTree(index: Int): SkillTree = skills[index]
    fun getSkillTree(material: Material): SkillTree? {
        return when(material) {
            Material.NETHERITE_PICKAXE -> skills[AbilityType.MINE.index]
            Material.NETHERITE_HOE -> skills[AbilityType.FARM.index]
            Material.FISHING_ROD -> skills[AbilityType.FISH.index]
            Material.NETHERITE_SWORD -> skills[AbilityType.HUNT.index]
            else -> null
        }
    }

    // -1 add Exp
    // -2 MAX_LEVEL SUCCESS
    // 0 >= Level UP
    fun addExp(exp: Long, type: AbilityType): Long {
        if(exp == 0L) return -1
        val typeIndex = type.index
        if(levels[typeIndex] >= MaxExps.maxLevel) {
            if(exps[typeIndex] >= MaxExps.getMaxExp(MaxExps.maxLevel)) {
                parseExp()
                return -1
            }
        }

        exps[typeIndex] += exp
        if(exps[typeIndex] <= 0) {
            exps[typeIndex] = 0
            parseExp()
            return -1
        }

        if(exps[typeIndex] >= MaxExps.getMaxExp(levels[typeIndex])) {
            if(levels[typeIndex] >= MaxExps.maxLevel) {
                exps[typeIndex] = MaxExps.getMaxExp(MaxExps.maxLevel)
                parseExp()
                return -2
            }

            val value = exps[typeIndex] - MaxExps.getMaxExp(levels[typeIndex])
            exps[typeIndex] = 0
            levels[typeIndex]++
            val skillTree = skills[typeIndex]
            when(levels[typeIndex]) {
                in 0 until 60 -> skillTree.point += 2
                60 -> skillTree.point+=3
                else -> skillTree.point++
            }
            parseLevel()
            return value
        }
        parseExp()
        return -1
    }

    fun setLevel(level: Int, type: AbilityType): Boolean {
        if(level > MaxExps.maxLevel || level < 0) return false
        levels[type.index] = level
        exps[type.index] = 0
        actionBar = parseActionBar()
        updateData()
        return true
    }

    fun reset() {
        for(i in 0 until 4) {
            levels[i] = 0
            exps[i] = 0
        }
        skills.forEach { it.reset(true) }
        updateData()
        actionBar = parseActionBar()
    }

    fun skillReset(type: AbilityType) {
        val tree = skills[type.index]
        val point = tree.reset(false)
        tree.point = point
        updateData()
    }

    fun updateData() {
        val d = PlayerDataManger.getPlayerData(uuid) ?: return
        d.set("ability.mine.level", getLevel(AbilityType.MINE))
        d.set("ability.mine.exp", getExp(AbilityType.MINE))
        d.set("ability.farm.level", getLevel(AbilityType.FARM))
        d.set("ability.farm.exp", getExp(AbilityType.FARM))
        d.set("ability.fish.level", getLevel(AbilityType.FISH))
        d.set("ability.fish.exp", getExp(AbilityType.FISH))
        d.set("ability.hunt.level", getLevel(AbilityType.HUNT))
        d.set("ability.hunt.exp", getExp(AbilityType.HUNT))
        d.set("ability.mine.skill", getSkillTree(AbilityType.MINE).toJson())
        d.set("ability.hunt.skill", getSkillTree(AbilityType.FARM).toJson())
        d.set("ability.fish.skill", getSkillTree(AbilityType.FISH).toJson())
        d.set("ability.hunt.skill", getSkillTree(AbilityType.HUNT).toJson())
    }

    private fun parseActionBar(): String {
        val skin = 0
        expBar = (base + getState(getExp(AbilityType.MINE), getMaxExp(AbilityType.MINE), skin) + getUnicode(skin, MINE_BASE) +"\uF80A"
                + getState(getExp(AbilityType.FARM), getMaxExp(AbilityType.FARM), skin) + getUnicode(skin, FARM_BASE) +"\uF80A"
                + getState(getExp(AbilityType.FISH), getMaxExp(AbilityType.FISH), skin)  + getUnicode(skin, FISH_BASE) +"\uF80A"
                + getState(getExp(AbilityType.HUNT), getMaxExp(AbilityType.HUNT), skin) + getUnicode(skin, HUNT_BASE))
        levelBar = (getLevelState(AbilityType.MINE, getLevel(AbilityType.MINE))
                + getLevelState(AbilityType.FARM, getLevel(AbilityType.FARM))
                + getLevelState(AbilityType.FISH, getLevel(AbilityType.FISH))
                + getLevelState(AbilityType.HUNT, getLevel(AbilityType.HUNT)))
        actionBarUpdate = true
        return expBar + levelBar
    }

    private fun parseExp() {
        if(!actionBarUpdate) {
            actionBar = parseActionBar()
            return
        }
        val skin = 0
        expBar = (base + getState(getExp(AbilityType.MINE), getMaxExp(AbilityType.MINE), skin) + getUnicode(skin, MINE_BASE) +"\uF80A"
                + getState(getExp(AbilityType.FARM), getMaxExp(AbilityType.FARM), skin) + getUnicode(skin, FARM_BASE) +"\uF80A"
                + getState(getExp(AbilityType.FISH), getMaxExp(AbilityType.FISH), skin)  + getUnicode(skin, FISH_BASE) +"\uF80A"
                + getState(getExp(AbilityType.HUNT), getMaxExp(AbilityType.HUNT), skin) + getUnicode(skin, HUNT_BASE))
        actionBar = expBar + levelBar
    }

    private fun parseLevel() {
        if(!actionBarUpdate) {
            actionBar = parseActionBar()
            return
        }
        levelBar = (getLevelState(AbilityType.MINE, getLevel(AbilityType.MINE))
                + getLevelState(AbilityType.FARM, getLevel(AbilityType.FARM))
                + getLevelState(AbilityType.FISH, getLevel(AbilityType.FISH))
                + getLevelState(AbilityType.HUNT, getLevel(AbilityType.HUNT)))
        actionBar = expBar + levelBar
    }

    companion object {
        val skins = arrayOf(
            HUDSkin("??f??????",arrayOf("\u00a1","\u00a2","\u00a4","\u00a3","\u00a5","\u00a6")),
        )
        const val MINE_BASE = 0
        const val FARM_BASE = 1
        const val FISH_BASE = 2
        const val HUNT_BASE = 3
        const val EXP_VOID = 4
        const val EXP_COLOR = 5
        private const val frontExp = "\uF82a\uF829\uF828\uF823"
        private const val tailExp = "\uF80a\uF809\uF808\uF801\uF803"
        private const val base = "\uf80b\uf809"
        private val numbers = arrayOf("\u03a0", "\u03a1", "\u03a2", "\u03a3", "\u03a4", "\u03a5", "\u03a6", "\u03a7", "\u03a8", "\u03a9")

        fun getUnicode(skinIndex: Int,index: Int): String = skins[skinIndex].array[index]

        private fun getState(exp: Long, maxExp: Long, skinIndex:Int): String {
            val per = exp.toDouble() / maxExp.toDouble()
            val res = per * 39
            val a =  res.roundToInt()
            val result = StringBuilder()
            for (i in 0..38) {
                result.append(frontExp).append(if (i < a) getUnicode(skinIndex, EXP_COLOR) else getUnicode(skinIndex,
                    EXP_VOID
                )
                ).append(tailExp)
            }
            return result.toString()
        }

        private fun getLevelCenter(level: Int): String {
            return when(level) {
                in 0 until 10-> numbers[level]
                in 10 until 100-> "\uF802${numbers[level / 10]}\uF802${numbers[level % 10]}\uF802"
                else -> {
                    var ten = level % 100
                    val one = ten % 10
                    ten /= 10
                    "\uF804${numbers[level / 100]}\uF802${numbers[ten]}\uF802${numbers[one]}\uF804"
                }
            }
        }

        private fun getLevelState(type: AbilityType, level: Int): String {
            return when (type) {
                AbilityType.MINE -> "\uF80c\uF80c\uF801${getLevelCenter(level)}"
                AbilityType.FARM -> "\uF82b\uF802${getLevelCenter(level)}"
                AbilityType.FISH -> "\uF82a\uF829\uF826\uF828${getLevelCenter(level)}"
                AbilityType.HUNT -> "\uF82b\uF802${getLevelCenter(level)}"
                else -> ""
            }
        }
    }

}
