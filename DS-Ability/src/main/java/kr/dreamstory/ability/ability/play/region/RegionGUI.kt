package kr.dreamstory.ability.ability.play.region

import kr.dreamstory.ability.api.DSCoreAPI
import com.google.common.collect.Lists
import kr.dreamstory.library.gui.DSGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.function.BiPredicate

class RegionGUI(region: Region, clone: Region?): DSGUI(9, "지역 관리", region, clone) {

    private lateinit var region: Region
    private lateinit var region_: Region
    private var tempClose: Boolean = false

    override fun firstInit() {
        region = getObject(0)!!
        region_ = getObject(1)?: region.clone()
    }

    override fun init() {
        setItem(region_.name, arrayListOf("§f이름 변경"), Material.GRASS_BLOCK,0)
        setItem(region_.des, arrayListOf("§f설명 변경"), Material.ACACIA_SIGN,1)
        setItem("§e스폰 위치 변경", null, Material.SPAWNER, 2)
        setItem("§f날씨 변경", arrayListOf("§f현재 날씨 : ${region_.weatherType.label}"),Material.SUNFLOWER, 4)
        setItem("§f타입 변경", arrayListOf("§f현재 타입 : ${region_.regionType.label}"), Material.SUNFLOWER, 5)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        if(rawSlot > 8) {
            return
        }
        val player = whoClicked as Player
        when(rawSlot) {
            0-> {
                tempClose = true
                DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "§f＆ 는 색 코드 입니다.", "§c취소 : - 입력"))
                    .response(BiPredicate { p, lines ->
                        var text = lines[0]
                        if(text == "-") {
                            RegionGUI(region,region_).open(player)
                            return@BiPredicate true
                        }
                        text = text.replace("&","§")
                        if(text.isBlank()) return@BiPredicate false
                        region_.name = text
                        RegionGUI(region,region_).open(player)
                        return@BiPredicate true
                    }).open(player)
            }
            1-> {
                tempClose = true
                DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "", "§c취소 : - 입력"))
                    .response(BiPredicate { p, lines ->
                        var text = lines[0]
                        if(text == "-") {
                            RegionGUI(region,region_).open(player)
                            return@BiPredicate true
                        }
                        text += lines[1] + lines[2]
                        text = text.replace("&","§")
                        if(text.isBlank()) return@BiPredicate false
                        region_.des = text
                        RegionGUI(region,region_).open(player)
                        return@BiPredicate true
                    }).open(player)
            }
            2-> {
                region_.spawn = player.location
                refresh()
            }
            3-> {
                if(isShiftClick) {
                    refresh()
                }
            }
            4-> {
                region_.weatherType = region_.weatherType.next
                refresh()
            }
            5-> {
                region_.regionType = region_.regionType.next
                refresh()
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {
        if(!tempClose && !region.equals(region_)) {
            player.sendMessage("§7정보가 갱신됩니다.")
            region_.update()
        }
    }

    override fun InventoryDragEvent.dragEvent() {
    }
}