package kr.dreamstory.ability.ability.play.skills.gui

import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import com.dreamstory.ability.core.GUI
import com.dreamstory.ability.extension.ability
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class SkillGUI(p: Player) : GUI(p, 27, "스킬") {

    companion object {
        val voidSlots = arrayOf(
             0,  1,  2,  3,  4,  5,  6,  7,  8,
             9,     11,     13,     15,     17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
        )
    }

    lateinit var ability: Ability

    override fun firstInit() { ability = player!!.ability!! }

    override fun init() {
        voidSlots.forEach { setVoidSlot(it) }
        setItem("§f채집",null, Material.IRON_HOE, 10)
        setItem("§f사냥",null, Material.IRON_SWORD, 12)
        setItem("§f낚시",null, Material.FISHING_ROD, 14)
        setItem("§f채광",null, Material.IRON_PICKAXE, 16)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        if(rawSlot < 27 && !voidSlots.contains(rawSlot) && rawSlot > 0) {
            if(currentItem == null) return
            val label = ChatColor.stripColor(currentItem!!.itemMeta.displayName)!!
            val type = AbilityType.labelOf(label)
            if(type == AbilityType.NONE) return
            val skillTree = ability.getSkillTree(type)
            whoClicked.closeInventory()
            SkillTreeGUI(whoClicked as Player, "스킬 - $label", skillTree, type)
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}


}