package kr.dreamstory.ability.ability.play.block.gui

import kr.dreamstory.ability.ability.play.block.FarmObject
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.core.GUI
import com.dreamstory.ability.manager.AbilityBlockManager
import com.google.common.collect.Lists
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.function.BiPredicate

class FarmGUI(p: Player, farmBlock: FarmObject): GUI(p, 9, "채집 블럭 설정",farmBlock) {

    lateinit var farmBlock: FarmObject

    override fun firstInit() {
        farmBlock = getObject(0)!!
    }

    override fun init() {
        val drop = farmBlock.drop
        setItem("§f드랍 아이템", arrayListOf(
            "§f현재 : ${drop.itemMeta?.displayName()?: drop.i18NDisplayName}",
            "",
            "§f인벤토리에서 드랍아이템을 클릭 시 해당 아이템으로 재설정됩니다."
        ),drop.type, 0, if(drop.itemMeta?.hasCustomModelData() == true) drop.itemMeta.customModelData else 0,1, false)

        setItem("§c삭제",null, Material.BARRIER, 8, 0, 1, false)
        setItem("§f최소 채집 시간", arrayListOf("§f현재 : ${farmBlock.minTime}", "", "§f클릭 시 편집할 수 있습니다."), Material.WOODEN_HOE, 1)
        setItem("§f기본 채집 시간", arrayListOf("§f현재 : ${farmBlock.time}", "", "§f클릭 시 편집할 수 있습니다."), Material.NETHERITE_HOE, 2)
        setItem("§f최소 레벨",arrayListOf("§f현재 : ${farmBlock.prevLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.APPLE, 3)
        setItem("§f피해량",arrayListOf("§f현재 : ${farmBlock.maxLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.GOLDEN_APPLE, 4)
        setItem("§f경험치", arrayListOf("§f현재 : ${farmBlock.exp}","","§f클릭 시 편집할 수 있습니다."),Material.EXPERIENCE_BOTTLE,5)
        setItem("§f사운드", arrayListOf("§f현재 : ${farmBlock.sound}","§f볼륨 : ${farmBlock.volume}","§f피치 : ${farmBlock.pitch}","","§7쉬프트 클릭 시 재생됩니다.","§f클릭 시 편집할 수 있습니다."),Material.NOTE_BLOCK,6)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val item = currentItem
        if(item == null || item.type == Material.AIR) return
        val slot = rawSlot
        if(slot >= 9) {
            if(farmBlock.setDropItem(item.clone())) {
                player?.sendMessage("§f등록 되었습니다.")
                refresh()
            } else {
                player?.sendMessage("§c드랍 아이템이 아닙니다.")
            }
        } else {
            when(slot) {
                1-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            p.sendMessage("통과")
                            val text = lines[0]
                            if(text == "-") {
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                farmBlock.minTime = time
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                2-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                farmBlock.time = time
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                3-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = Integer.parseInt(text)
                                farmBlock.prevLevel = time
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                4-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                farmBlock.maxLevel = time
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                5-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toLong()
                                farmBlock.exp = time
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                6-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "", ""))
                        .response(BiPredicate { p, lines ->
                            var text = lines[0]
                            if(text == "-") {
                                FarmGUI(player!!, farmBlock)
                                return@BiPredicate true
                            }

                            text += lines[1]
                            var check = false
                            try {
                                val sound = Sound.valueOf(text)
                                farmBlock.sound = sound
                                check = true
                            } catch (e: Exception) {}

                            try {
                                val volume = lines[2].toFloat()
                                farmBlock.volume = volume
                                check = true
                            } catch (e: Exception) {}

                            try {
                                val pitch = lines[3].toFloat()
                                farmBlock.pitch = pitch
                                check = true
                            } catch (e: Exception) {}
                            if(check) FarmGUI(player!!, farmBlock)
                            return@BiPredicate check
                        }).open(player!!)
                }
                8-> {
                    AbilityBlockManager.unregisterAbilityBlock(farmBlock)
                    player!!.closeInventory()
                }
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}
}