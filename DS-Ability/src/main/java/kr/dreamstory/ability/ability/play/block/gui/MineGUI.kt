package kr.dreamstory.ability.ability.play.block.gui

import kr.dreamstory.ability.ability.play.block.MineObject
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

class MineGUI(p: Player, mineBlock: MineObject): GUI(p, 9, "채굴 블럭 설정",mineBlock) {

    lateinit var mineBlock: MineObject

    override fun firstInit() {
        mineBlock = getObject(0)!!
    }

    override fun init() {
        val drop = mineBlock.drop
        setItem("§f드랍 아이템", arrayListOf(
            "§f현재 : ${drop.itemMeta?.displayName()?: drop.i18NDisplayName}",
            "",
            "§f인벤토리에서 드랍아이템을 클릭 시 해당 아이템으로 재설정됩니다."
        ),drop.type, 0, if(drop.itemMeta?.hasCustomModelData() == true) drop.itemMeta.customModelData else 0,1, false)

        setItem("§c삭제",null, Material.BARRIER, 8, 0, 1, false)
        setItem("§f최대 체력", arrayListOf("§f현재 : ${mineBlock.maxHealth}", "", "§f클릭 시 편집할 수 있습니다."), Material.WOODEN_PICKAXE, 1)
        setItem("§f최소 레벨",arrayListOf("§f현재 : ${mineBlock.prevLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.IRON_INGOT, 3)
        setItem("§f피해량",arrayListOf("§f현재 : ${mineBlock.maxLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.GOLD_INGOT, 4)
        setItem("§f경험치", arrayListOf("§f현재 : ${mineBlock.exp}","","§f클릭 시 편집할 수 있습니다."),Material.EXPERIENCE_BOTTLE,5)
        setItem("§f사운드", arrayListOf("§f현재 : ${mineBlock.sound}","§f볼륨 : ${mineBlock.volume}","§f피치 : ${mineBlock.pitch}","","§7쉬프트 클릭 시 재생됩니다.","§f클릭 시 편집할 수 있습니다."),Material.NOTE_BLOCK,6)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val item = currentItem
        if(item == null || item.type == Material.AIR) return
        val slot = rawSlot
        if(slot >= 9) {
            if(mineBlock.setDropItem(item.clone())) {
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
                            val hp = lines[0]
                            if(hp == "-") {
                                MineGUI(player!!, mineBlock)
                                return@BiPredicate true
                            }

                            try {
                                val health = hp.toDouble()
                                mineBlock.maxHealth = health
                                MineGUI(player!!, mineBlock)
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
                                MineGUI(player!!, mineBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = Integer.parseInt(text)
                                mineBlock.prevLevel = time
                                MineGUI(player!!, mineBlock)
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
                                MineGUI(player!!, mineBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                mineBlock.maxLevel = time
                                MineGUI(player!!, mineBlock)
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
                                MineGUI(player!!, mineBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toLong()
                                mineBlock.exp = time
                                MineGUI(player!!, mineBlock)
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
                                MineGUI(player!!, mineBlock)
                                return@BiPredicate true
                            }

                            text += lines[1]
                            var check = false
                            try {
                                val sound = Sound.valueOf(text)
                                mineBlock.sound = sound
                                check = true
                            } catch (e: Exception) {}

                            try {
                                val volume = lines[2].toFloat()
                                mineBlock.volume = volume
                                check = true
                            } catch (e: Exception) {}

                            try {
                                val pitch = lines[3].toFloat()
                                mineBlock.pitch = pitch
                                check = true
                            } catch (e: Exception) {}
                            if(check) MineGUI(player!!, mineBlock)
                            return@BiPredicate check
                        }).open(player!!)
                }
                8-> {
                    AbilityBlockManager.unregisterAbilityBlock(mineBlock)
                    player!!.closeInventory()
                }
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}
}