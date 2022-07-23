package kr.dreamstory.community.gui

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.friend.FriendManager
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.gui.DSGUI
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import java.util.function.BiPredicate

class CommunityGUI(player: Player): DSGUI(27,"커뮤니티 설정",player) {

    private lateinit var player: Player

    override fun firstInit() {
        player = getObjectFx(0)
    }

    override fun init() {
        setItem(10,ItemStack(Material.PLAYER_HEAD))
        setItem(11,ItemStack(Material.BARRIER))
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        when(rawSlot) {
            10 -> FriendGUI(player).open(player)
            11 -> IgnoreGUI(player).open(player)
        }
    }
    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() { isCancelled = true }
}