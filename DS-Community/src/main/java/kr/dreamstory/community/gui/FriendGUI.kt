package kr.dreamstory.community.gui

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.friend.FriendManager
import kr.dreamstory.community.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.extension.getStringNbt
import kr.dreamstory.library.extension.setNameAndLore
import kr.dreamstory.library.extension.setStringNbt
import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.item.minecraft.api.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import kotlin.collections.ArrayList

class FriendGUI(player: Player): DSGUI(36,"친구 관리",player) {

    lateinit var player: Player
    override fun firstInit() {
        player = getObjectFx(0)
    }

    var page = 0

    override fun init() {
        main.schedule(SynchronizationContext.ASYNC) {
            waitFor(1)
            val items = getFriendsHeads()
            val glass = ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE," ")
            for(slot in 27..35) setItem(slot,glass)
            setItem(31,ItemStackBuilder.build(Material.PAPER,"추가"))
            val startSlot = 27 * page
            var slot = 0
            for(index in startSlot..startSlot + 26) {
                setItem(slot,items.getOrNull(index) ?: break)
                slot ++
            }
            if(page > 0) {
                setItem(27,ItemStackBuilder.build(Material.PAPER,"이전 페이지"))
            }
            if(items.getOrNull(startSlot + 27) != null) {
                setItem(35,ItemStackBuilder.build(Material.PAPER,"다음 페이지"))
            }
        }
    }

    private fun getFriendsHeads(): List<ItemStack> {
        val uuid = player.uniqueId
        val state = CommunityManager.getCommunityData(uuid)!!
        val list = ArrayList<ItemStack>()
        for(fUUID in state.friends) {
            val fData = PlayerDataManger.getOfflinePlayerData(fUUID)!!
            val head = fData.head ?: continue
            head.setNameAndLore("§e${fData.name}", listOf(
                "좌클릭 - 귓속말",
                "우클릭 - 친구 삭제"
            ))
            head.setStringNbt("name",fData.name!!)
            list.add(head)
        }
        return list
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val item = currentItem
        if(item.isEmpty()) return
        if(rawSlot >= 36) return
        when(rawSlot) {
            27 -> {
                if(item!!.type == Material.PAPER) {
                    page --
                    refresh(true)
                }
                return
            }
            35 -> {
                if(item!!.type == Material.PAPER) {
                    page ++
                    refresh(true)
                }
                return
            }
        }
        if(item!!.type == Material.PLAYER_HEAD) {
            if(isLeftClick) {
                FriendManager.remove(player,item.getStringNbt("name")!!)
                refresh(true)
            }
        }
    }
    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() { isCancelled = true }

}