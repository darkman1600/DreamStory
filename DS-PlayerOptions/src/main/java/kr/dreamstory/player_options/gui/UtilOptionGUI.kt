package kr.dreamstory.player_options.gui

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.extension.setNameAndLore
import kr.dreamstory.library.gui.DSGUI
import kr.dreamstory.library.utils.sound.SoundManager
import kr.dreamstory.player_options.listener.UtilOptionListener
import kr.dreamstory.player_options.main
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

class UtilOptionGUI(player: Player): DSGUI(27,"편의기능 설정",player) {

    private lateinit var player: Player

    override fun firstInit() {
        player = getObjectFx(0)
    }

    override fun init() {
        setItem(10,getItemPickupToggleButton())
        setItem(11,getItemDropToggleButton())
    }

    private fun getItemPickupToggleButton(): ItemStack {
        val str = if(!UtilOptionListener.pickupDetectSet.contains(player.uniqueId)) "§a활성화" else "§7비활성화"
        return ItemStack(Material.PAPER).setNameAndLore("§e아이템 줍기", listOf("§f현재 상태 : $str"))
    }
    private fun getItemDropToggleButton(): ItemStack {
        val str = if(!UtilOptionListener.dropDetectSet.contains(player.uniqueId)) "§a활성화" else "§7비활성화"
        return ItemStack(Material.PAPER).setNameAndLore("§e아이템 버리기", listOf("§f현재 상태 : $str"))
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        when(rawSlot) {
            10 -> {
                val uuid = player.uniqueId
                val bool = UtilOptionListener.pickupDetectSet.contains(uuid)
                if (bool) {
                    UtilOptionListener.pickupDetectSet.remove(uuid)
                    SoundManager.turnOn(player)
                } else {
                    UtilOptionListener.pickupDetectSet.add(uuid)
                    SoundManager.turnOff(player)
                }
                PlayerDataManger.getPlayerData(uuid)!!.set("option.pickup",!bool)
                refresh(true)
            }
            11 -> {
                val uuid = player.uniqueId
                val bool = UtilOptionListener.dropDetectSet.contains(uuid)
                if (bool) {
                    UtilOptionListener.dropDetectSet.remove(uuid)
                    SoundManager.turnOn(player)
                } else {
                    UtilOptionListener.dropDetectSet.add(uuid)
                    SoundManager.turnOff(player)
                }
                PlayerDataManger.getPlayerData(uuid)!!.set("option.drop",!bool)
                refresh(true)
            }
        }
    }
    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}


}