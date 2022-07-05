package kr.dreamstory.ability.ability.play.skills.gui

import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.ability.SkillTree
import kr.dreamstory.ability.ability.play.command.InputType
import kr.dreamstory.ability.ability.play.skills.Active
import kr.dreamstory.ability.ability.play.skills.EnumSkill
import kr.dreamstory.ability.manager.CommandManager
import com.dreamstory.ability.manager.SkillManager
import kr.dreamstory.library.gui.DSGUI
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class SkillTreeGUI(title: String, skillTree: SkillTree, abilityType: AbilityType) : DSGUI(36, title,skillTree,abilityType) {

    private lateinit var skillTree: SkillTree
    private lateinit var abilityType: AbilityType
    private lateinit var buttons: HashMap<Int, EnumSkill>

    override fun firstInit() {
        skillTree = getObjectFx(0);
        abilityType = getObjectFx(1)
        buttons = HashMap()
    }

    override fun init() {
        if(buttons.isNotEmpty()) buttons.clear()
        for(i in 0 until 27) setVoidSlot(i)
        EnumSkill.getSkillList(abilityType).forEach {
            val slot = it.slot
            inv.setItem(slot, SkillManager.getSkill(it)!!.getIcon(skillTree.getSkillLevel(it), skillTree.getCommand(it)), )
            buttons[slot] = it
        }

        for(i in 28 until 35) setItem("§f",null, Material.BLACK_STAINED_GLASS_PANE, i)
        setResetButton(27,"§c스킬 초기화",null)
        setQuestionMark(35,"§f도움말", SkillManager.getDescription(abilityType))
        setItem("§f정보", arrayListOf("",
        "§f스킬 포인트 : ${skillTree.point}"), Material.ENCHANTED_BOOK, 31)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        if(rawSlot == 27) {
            //reset
        } else {
            if(buttons.containsKey(rawSlot)) {
                val es = buttons[rawSlot]!!
                val skill = SkillManager.getSkill(es) ?: return
                val player = whoClicked as Player
                fun click(suc: Boolean = true) {
                    val sound = if(suc) Sound.BLOCK_LEVER_CLICK else Sound.BLOCK_NOTE_BLOCK_PLING
                    player.playSound(player.location, sound, 1F, 0.8F)
                }
                if(isRightClick) {
                    // command
                    if(skill is Active) {
                        if(skillTree.getSkillLevel(es) == 0) {
                            click(false)
                            player.sendMessage("§c스킬 레벨이 0 입니다.")
                        }
                        else {
                            whoClicked.closeInventory()
                            CommandManager.put(player, skillTree, es, InputType.INSERT, 1)
                        }
                    }
                } else if(isLeftClick) {
                    // add level
                    if(skillTree.point == 0) {
                        click(false)
                        player.sendMessage("§c스킬 포인트가 부족합니다.")
                    } else {
                        if(skill.maxLevel == skillTree.getSkillLevel(es)) {
                            player.sendMessage("§c이미 최대 레벨입니다.")
                        } else {
                            click()
                            skillTree.addSkillLevel(es)
                            refresh()
                        }
                    }
                }
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {
    }

    override fun InventoryDragEvent.dragEvent() {
    }
}