package kr.dreamstory.ability.ability.play.block.gui

import kr.dreamstory.ability.ability.play.block.FishObject
import kr.dreamstory.ability.manager.AbilityBlockManager
import com.google.common.collect.Lists
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.gui.DSGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.function.BiPredicate

class FishGUI(fishBlock: FishObject): DSGUI(9, "낚시 바이옴 설정",fishBlock) {

    lateinit var fishBlock: FishObject

    override fun firstInit() {
        fishBlock = getObject(0)!!
    }

    override fun init() {
        val drop = fishBlock.drop
        setItem("§f드랍 아이템", arrayListOf(
            "§f현재 : ${drop.itemMeta?.displayName()?: drop.i18NDisplayName}",
            "",
            "§f인벤토리에서 드랍아이템을 클릭 시 해당 아이템으로 재설정됩니다."
        ),drop.type, 0, if(drop.itemMeta?.hasCustomModelData() == true) drop.itemMeta.customModelData else 0,1, false)

        setItem("§c삭제",null, Material.BARRIER, 8, 0, 1, false)
        setItem("§f기본 체력", arrayListOf("§f현재 : ${fishBlock.health}", "","§c±5 의 값을 가집니다.", "§f클릭 시 편집할 수 있습니다."), Material.FISHING_ROD, 1)
        setItem("§f최대 체력", arrayListOf("§f현재 : ${fishBlock.maxHealth}", "", "§f클릭 시 편집할 수 있습니다."), Material.FISHING_ROD, 2)
        setItem("§f최소 레벨",arrayListOf("§f현재 : ${fishBlock.prevLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.SALMON, 3)
        setItem("§f피해량",arrayListOf("§f현재 : ${fishBlock.maxLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.PUFFERFISH, 4)
        setItem("§f경험치",arrayListOf("§f현재 : ${fishBlock.exp}","","§f클릭 시 편집할 수 있습니다."),Material.EXPERIENCE_BOTTLE,5)
        setItem("§f물고기 힘",arrayListOf("§f현재 : ${fishBlock.minPower} ~ ${fishBlock.maxPower}","§f클릭 시 편집할 수 있습니다."),Material.TROPICAL_FISH,6)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val item = currentItem
        if(item == null || item.type == Material.AIR) return
        val slot = rawSlot
        val player = whoClicked as Player
        if(slot >= 9) {
            if(fishBlock.setDropItem(item.clone())) {
                player.sendMessage("§f등록 되었습니다.")
                refresh()
            } else {
                player.sendMessage("§c드랍 아이템이 아닙니다.")
            }
        } else {
            when(slot) {
                1-> {
                    player.closeInventory()
                    DSLibraryAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if (text.equals("-", ignoreCase = true)) {
                                FishGUI(fishBlock).open(player)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                fishBlock.health = time
                                FishGUI(fishBlock).open(player)
                                true
                            } catch (ex: NumberFormatException) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                false
                            }
                        }).open(player)
                }
                2-> {
                    player.closeInventory()
                    DSLibraryAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if (text.equals("-", ignoreCase = true)) {
                                FishGUI(fishBlock).open(player)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                fishBlock.maxHealth = time
                                FishGUI(fishBlock).open(player)
                                true
                            } catch (ex: NumberFormatException) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                false
                            }
                        }).open(player)
                }
                3-> {
                    player.closeInventory()
                    DSLibraryAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if (text.equals("-", ignoreCase = true)) {
                                FishGUI(fishBlock).open(player)
                                return@BiPredicate true
                            }

                            try {
                                val time = Integer.parseInt(text)
                                fishBlock.prevLevel = time
                                FishGUI(fishBlock).open(player)
                                true
                            } catch (ex: NumberFormatException) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                false
                            }
                        }).open(player)
                }
                4-> {
                    player.closeInventory()
                    DSLibraryAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if (text.equals("-", ignoreCase = true)) {
                                FishGUI(fishBlock).open(player)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                fishBlock.maxLevel = time
                                FishGUI(fishBlock).open(player)
                                true
                            } catch (ex: NumberFormatException) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                false
                            }
                        }).open(player)
                }
                5-> {
                    player.closeInventory()
                    DSLibraryAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if (text.equals("-", ignoreCase = true)) {
                                FishGUI(fishBlock).open(player)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toLong()
                                fishBlock.exp = time
                                FishGUI(fishBlock).open(player)
                                true
                            } catch (ex: NumberFormatException) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                false
                            }
                        }).open(player)
                }
                6-> {
                    player.closeInventory()
                    DSLibraryAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "1. 최소 2. 최대", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if (text.equals("-", ignoreCase = true)) {
                                FishGUI(fishBlock).open(player)
                                return@BiPredicate true
                            }

                            var check = false

                            try {
                                val v1 = text.toDouble()
                                fishBlock.minPower = v1
                                check = true
                            } catch (ex: NumberFormatException) {}

                            try {
                                val v2 = lines[1].toDouble()
                                fishBlock.maxPower = v2
                                check = true
                            } catch (ex: NumberFormatException) {}

                            if(check) FishGUI(fishBlock).open(player)
                            check
                        }).open(player)
                }
                8-> {
                    AbilityBlockManager.unregisterAbilityBlock(fishBlock)
                    player.closeInventory()
                }
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}
}