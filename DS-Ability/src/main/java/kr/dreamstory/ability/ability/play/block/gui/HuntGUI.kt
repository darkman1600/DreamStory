package kr.dreamstory.ability.ability.play.block.gui

import kr.dreamstory.ability.ability.play.block.HuntObject
import kr.dreamstory.ability.ability.play.block.obj.MobType
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.core.GUI
import com.dreamstory.ability.manager.AbilityBlockManager
import com.google.common.collect.Lists
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.function.BiPredicate

class HuntGUI(p: Player, huntBlock: HuntObject): GUI(p, 9, huntBlock.mobName,huntBlock) {

    lateinit var huntBlock: HuntObject

    override fun firstInit() {
        huntBlock = getObject(0)!!
    }

    override fun init() {
        val drop = huntBlock.drop
        setItem("§f드랍 아이템", arrayListOf(
            "§f현재 : ${drop.itemMeta?.displayName()?: drop.i18NDisplayName}",
            "",
            "§f인벤토리에서 드랍아이템을 클릭 시 해당 아이템으로 재설정됩니다."
        ),drop.type, 0, if(drop.itemMeta?.hasCustomModelData() == true) drop.itemMeta.customModelData else 0,1, false)

        setItem("§c삭제",null, Material.BARRIER, 8)
        inv.setItem(1, huntBlock.mobType.invIcon)
        setItem("§f던전 이름", arrayListOf("§f현재 : ${huntBlock.dungeonName}", "", "§f클릭 시 편집할 수 있습니다."), Material.SOUL_CAMPFIRE, 2)
        setItem("§f최소 레벨", arrayListOf("§f현재 : ${huntBlock.prevLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.WOODEN_SWORD, 3)
        setItem("§f최대 레벨",arrayListOf("§f현재 : ${huntBlock.maxLevel}", "", "§f클릭 시 편집할 수 있습니다."), Material.NETHERITE_SWORD, 4)
        setItem("§f경험치",arrayListOf("§f현재 : ${huntBlock.exp}", "", "§f클릭 시 편집할 수 있습니다."), Material.EXPERIENCE_BOTTLE, 5)
        if(huntBlock.mobType == MobType.BOSS1) setItem("§fn 값 설정", arrayListOf("§f현재 : ${huntBlock.bossN}","","§f클릭 시 편집할 수 있습니다."),Material.GOLDEN_APPLE,6)
        setItem("§f미스틱 몹 이름", arrayListOf("§f${huntBlock.mobName}", "", "§c설정할 수 없습니다."), Material.WITHER_SKELETON_SKULL, 7)
    }

    override fun InventoryClickEvent.clickEvent() {
        isCancelled = true
        val item = currentItem
        if(item == null || item.type == Material.AIR) return
        val slot = rawSlot
        if(slot >= 9) {
            if(huntBlock.setDropItem(item.clone())) {
                player?.sendMessage("§f등록 되었습니다.")
                refresh()
            } else {
                player?.sendMessage("§c드랍 아이템이 아닙니다.")
            }
        } else {
            when(slot) {
                1-> {
                    huntBlock.setType()
                    refresh()
                }
                2-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "삭제 : ! 입력", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            }

                            if(text == "!") {
                                huntBlock.dungeonName = ""
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            }

                            if(text == "") { return@BiPredicate false }

                            huntBlock.dungeonName = text
                            HuntGUI(player!!, huntBlock)
                            return@BiPredicate true
                        }).open(player!!)
                }
                3-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = Integer.parseInt(text)
                                huntBlock.prevLevel = time
                                HuntGUI(player!!, huntBlock)
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
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toDouble()
                                huntBlock.maxLevel = time
                                HuntGUI(player!!, huntBlock)
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
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = text.toLong()
                                huntBlock.exp = time
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                6-> {
                    player!!.closeInventory()
                    DSCoreAPI.signMenuFactory.newMenu(Lists.newArrayList("", "", "숫자만 입력하세요.", "§c취소 : - 입력"))
                        .response(BiPredicate { p, lines ->
                            val text = lines[0]
                            if(text == "-") {
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            }

                            try {
                                val time = Integer.parseInt(text)
                                if(time < 1) {
                                    player!!.sendMessage("§c1보다 작을 수 없습니다.")
                                    return@BiPredicate false
                                }
                                huntBlock.bossN = time
                                HuntGUI(player!!, huntBlock)
                                return@BiPredicate true
                            } catch (e: Exception) {
                                p.sendMessage("§c숫자만 입력 가능합니다.")
                                return@BiPredicate false
                            }
                        }).open(player!!)
                }
                8-> {
                    AbilityBlockManager.unregisterAbilityBlock(huntBlock)
                    player!!.closeInventory()
                }
            }
        }
    }

    override fun InventoryCloseEvent.closeEvent() {}
    override fun InventoryDragEvent.dragEvent() {}
}