package kr.dreamstory.ability.ability.play.island.gui

import kr.dreamstory.ability.ability.play.island.DSIsland
import kr.dreamstory.ability.ability.play.island.data.DSIslandOption
import kr.dreamstory.ability.ability.play.island.data.IslandPermissionType
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.core.GUI
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.util.longFormat
import kr.dreamstory.ability.java.pixelmaker.gui.PixelGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class IslandMainGUI(p: Player, island: DSIsland) : GUI(p, 9, "섬 메뉴",island) {

    lateinit var island: DSIsland
    lateinit var option: DSIslandOption

    override fun firstInit() { island = getObjectFx(0) }

    override fun init() {
        option = island.option.copy()
        setItem(island.iconLabel + island.name,
            arrayListOf("",
                "§f${island.ownerLabel} §f: §b${DSCoreAPI.getCustomName(island.owner)}",
                "§f섬 순위 : §a${island.rank}",
                "§f섬 레벨 : §e${island.level.longFormat()}",
                "§f섬 경험치 : §e${island.nowExp.longFormat()}",
                "§f섬 인원 : §7${island.members.size} §f/ §e${island.maxSize}",
                "§f섬 챌린지 : §7${island.challengeCount} §f/ §e3",
                "",
                "§7§o클릭 시, 섬으로 이동합니다."
            ), Material.GRASS_BLOCK, 0)
        setItem("§f섬원", arrayListOf("","§7§o섬원 목록을 확인할 수 있습니다."),Material.PLAYER_HEAD, 1)
        setItem("§f게시판", arrayListOf("","§f섬원들과 소통할 수 있는 게시판 입니다.","§7§o섬 게시판을 엽니다."),Material.PAPER, 2, 2)
        setItem("§f포인트 상점", arrayListOf("","§f섬 포인트를 사용하여 다양한 아이템을 구매하세요.","§7§o포인트 상점을 엽니다."),Material.PAPER, 3, 2)
        setItem("§f섬 순위", arrayListOf("","§7§o섬 순위 Top10 을 확인할 수 있습니다."),Material.PAPER, 4, 2)

        if(island.hasPermission(IslandPermissionType.ICON, player!!.id))
            setItem("§f아이콘 편집", arrayListOf("","§f섬 아이콘은 채팅과 섬 이름앞에 표시됩니다.","§7§o클릭하여 편집할 수 있습니다."), Material.PAPER, 7, 2)
        if(island.owner == player!!.id)
            setItem("§f설정", arrayListOf("","§7§o섬 설정창을 엽니다."),Material.PAPER, 8, 2)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val p = whoClicked as Player
        if(!island.members.contains(p.id)) {
            p.closeInventory()
            return
        }

        val item = currentItem?: return
        if(item.type == Material.AIR) return
        when(rawSlot) {
            0-> {
                p.closeInventory()
                island.gotoIsland(p)
            }
            1-> {
                //member GUI
                p.closeInventory()
            }
            2-> {
                // board GUI
                p.closeInventory()
            }
            3-> {
                // shop GUI
                p.closeInventory()
            }
            4-> {
                // rank GUI
                p.closeInventory()
            }
            7-> {
                // icon GUI
                p.closeInventory()
                if(island.hasPermission(IslandPermissionType.ICON, p.id)) {
                    PixelGUI(p.uniqueId, "", island)
                }
            }
            8-> {
                // setting GUI
                p.closeInventory()
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {
    }

    override fun InventoryDragEvent.dragEvent() {
    }
}