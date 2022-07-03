package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.SkillTree
import kr.dreamstory.ability.ability.play.command.InputType
import kr.dreamstory.ability.ability.play.command.SkillCommand
import kr.dreamstory.ability.ability.play.region.RegionType
import kr.dreamstory.ability.ability.play.skills.Active
import kr.dreamstory.ability.ability.play.skills.EnumSkill
import com.dreamstory.ability.extension.ability
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

object CommandManager {

    const val RIGHT = 1
    const val LEFT = 2
    const val SHIFT = 3
    const val F = 4
    const val Q = 5

    private val subTitle = listOf("\u2561", "\u2562", "\u2563", "\u2564", "\u2565")
    private val activeMaterials = listOf(
        Material.NETHERITE_PICKAXE,
        Material.NETHERITE_HOE,
        Material.NETHERITE_SWORD,
        Material.FISHING_ROD
    )

    fun isAbilityTool(material: Material): Boolean = listOf(Material.NETHERITE_HOE,Material.NETHERITE_PICKAXE,Material.NETHERITE_SWORD,Material.FISHING_ROD).contains(material)

    val cmdMap = HashMap<UUID, SkillCommand>()
    val delayList: HashSet<UUID> = HashSet()
    fun setDelay(p: Player, tick: Long) {
        delayList.add(p.uniqueId)
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(tick)
            delayList.remove(p.uniqueId)
        }
    }

    fun check(p: Player): Boolean = cmdMap.containsKey(p.uniqueId)
    fun itemCheck(p: Player): Boolean = activeMaterials.contains(p.inventory.itemInMainHand.type)

    val Player.commandText: SkillCommand get() = cmdMap[uniqueId]!!
    fun getText(label: Int): String = subTitle[label - 1]

    fun getCommandText(command:Int): String {
        if(command < 111) return ""
        val nums = arrayOf(0,0,0)
        nums[1] = command / 10
        nums[0] = nums[1] / 10
        nums[1] = nums[1] % 10
        nums[2] = command % 10
        return "${getText(nums[0])} ${getText(nums[1])} ${getText(nums[2])}"
    }

    fun action(p: Player, sc: SkillCommand, type: RegionType) {
        if(sc.isEqualsType(type)) {
            val current = sc.getFinal()
            if(sc.inputType == InputType.INSERT) {
                if(sc.skillTree!!.setCommand(current, sc.enumSkill!!)) {
                    p.sendTitle("§a등록 됨", null, 0, 40, 20)
                } else {
                    p.sendTitle("§c중복 됨", null, 0, 40, 20)
                }
                cmdMap.remove(p.uniqueId)
            } else {
                main.server.scheduler.schedule(main) {
                    waitFor(1)
                    try {
                        val hand = p.inventory.itemInMainHand
                        val tree = p.ability?.getSkillTree(hand.type) ?: return@schedule
                        val skill = tree.getSkill(current)
                        if (skill == null || !isAbilityTool(hand.type)) p.sendTitle("no", null, 0, 40, 20)
                        else {
                            val level = tree.getSkillLevel(skill.skillIndex)
                            p.sendTitle(skill.getSimpleName(level), null, 0, 40, 20)
                            if (skill is Active) {
                                if(p.hasCooldown(skill.getCoolTimeIcon())) {
                                    p.sendTitle(null, "§e${p.getCooldown(skill.getCoolTimeIcon()) / 20} 초 §c후에 사용하세요.")
                                }
                                else {
                                    p.setCooldown(skill.getCoolTimeIcon(), skill.getCoolTime(level).toInt())
                                    skill.action(level, p)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        p.sendTitle("no", null, 0, 40, 20)
                    }
                    cmdMap.remove(p.uniqueId)
                }
            }
            setDelay(p, 20)
        } else setDelay(p, 1L)
    }

    fun put(p: Player, tree: SkillTree, skill: EnumSkill, type: InputType, action: Int) {
        val uuid = p.uniqueId
        val sc = SkillCommand(p, type, getText(action),null,tree,skill)
        if(!sc.register()) return
        cmdMap[uuid] = sc
        val time = System.currentTimeMillis()
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            repeating(2)
            while(true) {
                if(!p.isOnline || !cmdMap.containsKey(uuid)) break
                else {
                    if(time + 2000 <= System.currentTimeMillis()) {
                        cmdMap.remove(uuid)
                        p.sendTitle("§c입력시간 초과", null, 0, 40, 20)
                        p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.5f)
                        setDelay(p, 20L)
                        break
                    }
                    yield()
                }
            }
        }
    }
    fun put(p: Player, region: RegionType, type: InputType, action: Int) {
        val uuid = p.uniqueId
        val sc = SkillCommand(p, type, getText(action),region)
        if(!sc.register()) return
        cmdMap[uuid] = sc
        val time = System.currentTimeMillis()
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            repeating(2)
            while(true) {
                if(!p.isOnline || !cmdMap.containsKey(uuid)) break
                else {
                    if(time + 2000 <= System.currentTimeMillis()) {
                        cmdMap.remove(uuid)
                        p.sendTitle("§c입력시간 초과", null, 0, 40, 20)
                        p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.5f)
                        setDelay(p, 20L)
                        break
                    }
                    yield()
                }
            }
        }
    }

}