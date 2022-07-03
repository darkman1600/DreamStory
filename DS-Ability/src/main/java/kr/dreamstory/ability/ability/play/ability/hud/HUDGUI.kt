package kr.dreamstory.ability.ability.play.ability.hud

import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.data.PlayerOption
import com.dreamstory.ability.core.GUI
import com.dreamstory.ability.extension.ability
import com.dreamstory.ability.extension.playerOption
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class HUDGUI(p: Player): GUI(p, 9, "경험치 스킨") {

    lateinit var option: PlayerOption

    override fun firstInit() {
        option = player!!.playerOption.copy()
    }

    override fun init() {
        Ability.skins.forEachIndexed { index, skin->
            setItem(skin.name,arrayListOf(
                "§f${Ability.getUnicode(index, Ability.FARM_BASE)}","","",
                "§f${Ability.getUnicode(index, Ability.HUNT_BASE)}","","",
                "§f${Ability.getUnicode(index, Ability.FISH_BASE)}","","",
                "§f${Ability.getUnicode(index, Ability.MINE_BASE)}","",""
            ), Material.BOOK,index,0, 1, option.skinNumber == index)
        }
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        if(option.skinNumber == rawSlot) whoClicked.sendMessage("§c이미 선택 된 스킨입니다.")
        else {
            if(rawSlot < 9) {
                val item = currentItem
                if(item != null && item.type != Material.AIR) {
                    option.skinNumber = rawSlot
                    player!!.ability?.updateActionBar(rawSlot)
                    refresh()
                }
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {
        val op = (player as Player).playerOption
        if(op != option) option.update()
    }

    override fun InventoryDragEvent.dragEvent() {
    }

}