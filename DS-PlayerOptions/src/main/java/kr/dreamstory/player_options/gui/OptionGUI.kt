package kr.dreamstory.player_options.gui

import kr.dreamstory.community.gui.CommunityGUI
import kr.dreamstory.library.extension.setNameAndLore
import kr.dreamstory.library.gui.DSGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

class OptionGUI: DSGUI(27,"개인 설정") {

    override fun init() {
        setItem(10, ItemStack(Material.PLAYER_HEAD).setNameAndLore("§e커뮤니티 설정", listOf("§f친구, 차단 등을 관리할 수 있습니다.")))
        setItem(11,ItemStack(Material.PAINTING).setNameAndLore("§e디스플레이 설정", listOf("§f화면에 보이는 내용을 변경할 수 있습니다.")))
        setItem(12,ItemStack(Material.REPEATER).setNameAndLore("§e편의기능 설정", listOf("§f아이템 줍기, 버리기 등 편의 기능을 설정할 수 있습니다.")))
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
            12 -> {
                UtilOptionGUI(player).open(player)
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() { isCancelled = true }

}