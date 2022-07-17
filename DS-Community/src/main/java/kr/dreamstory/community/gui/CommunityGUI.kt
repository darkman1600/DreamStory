package kr.dreamstory.community.gui

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.friend.FriendManager
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.gui.DSGUI
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.function.BiPredicate

class CommunityGUI(val player: Player): DSGUI(54,"커뮤니티 설정") {

    override fun init() {

    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        when(rawSlot) {
            0 -> FriendGUI(player).open(player)
            1 -> IgnoreGUI()
        }
    }
    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() { isCancelled = true }
}