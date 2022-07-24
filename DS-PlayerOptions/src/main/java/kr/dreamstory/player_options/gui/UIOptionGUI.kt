package kr.dreamstory.player_options.gui

import kr.dreamstory.ability.extension.ability
import kr.dreamstory.ability.manager.AbilityManager
import kr.dreamstory.ability.manager.ActionBarManager
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.utils.sound.SoundManager
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

class UIOptionGUI(player: Player): DSGUI(27,"디스플레이 설정",player) {

    private lateinit var player: Player

    override fun firstInit() {
        player = getObjectFx(0)
    }

    override fun init() {
        setItem(10,getActionBarToggleButton())
    }

    private fun getActionBarToggleButton(): ItemStack {
        val base = ItemStack(Material.LEVER)
        val meta = base.itemMeta
        meta.setDisplayName("경험치 / 레벨 UI 표기")
        val str = if(ActionBarManager.actionBarSet.contains(player)) "§a활성화" else "§7비활성화"
        meta.lore = listOf("§f현재상태 : $str")
        base.itemMeta = meta
        return base
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        when(rawSlot) {
            10 -> {
                val contains = ActionBarManager.actionBarSet.contains(player)
                if(contains) {
                    ActionBarManager.actionBarSet.remove(player)
                    SoundManager.turnOff(player)
                } else {
                    ActionBarManager.actionBarSet.add(player)
                    SoundManager.turnOn(player)
                }
                PlayerDataManger.getPlayerData(player.uniqueId)!!.set("option.ability_actionbar_toggle",!contains)
                player.sendActionBar(Component.text(""))
                refresh(true)
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}

    override fun InventoryDragEvent.dragEvent() { isCancelled = true }
}