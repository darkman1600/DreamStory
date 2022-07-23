package kr.dreamstory.player_options.gui

import kr.dreamstory.community.gui.CommunityGUI
import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.player_options.options.AbilityOptions
import kr.dreamstory.player_options.options.OptionType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

class OptionGUI: DSGUI(27,"개인 설정") {

    override fun init() {
        setItem(10, ItemStack(Material.PLAYER_HEAD))
        setItem(11,ItemStack(Material.REPEATER))
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val player = whoClicked as Player
        when(rawSlot) {
            10 -> {
                CommunityGUI(player).open(player)
            }
            11 -> {
                UIOptionGUI(player).open(player)
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() { isCancelled = true }

}