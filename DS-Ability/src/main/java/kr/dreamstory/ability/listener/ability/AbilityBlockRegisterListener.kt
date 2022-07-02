package com.dreamstory.ability.listener.ability

import kr.dreamstory.ability.ability.play.ability.AbilityType
import com.dreamstory.ability.ability.play.block.*
import kr.dreamstory.ability.ability.play.block.gui.FarmGUI
import kr.dreamstory.ability.ability.play.block.gui.FishGUI
import kr.dreamstory.ability.ability.play.block.gui.HuntGUI
import kr.dreamstory.ability.ability.play.block.gui.MineGUI
import kr.dreamstory.ability.ability.play.region.RegionType
import com.dreamstory.ability.extension.region
import com.dreamstory.ability.listener.interfaces.ChannelListener
import com.dreamstory.ability.manager.AbilityBlockManager
import com.dreamstory.ability.objs.AbilityBlockStick
import io.lumine.mythic.bukkit.MythicBukkit
import kr.dreamstory.ability.ability.play.block.*
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class AbilityBlockRegisterListener: ChannelListener {

    @EventHandler
    fun toolClick(e: PlayerInteractEvent) {
        val p = e.player
        val action = e.action
        val hand = e.hand
        if(!p.isOp || (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR)) return
        if(hand == null || hand != EquipmentSlot.HAND) return

        val tool = p.inventory.itemInMainHand
        if(!AbilityBlockStick.isStick(tool)) return

        val region = p.region

        if(region == null) {
            p.sendMessage("§c지역에 들어가서 설정하세요.")
            return
        }
        if(region.regionType != RegionType.FIELD) {
            p.sendMessage("§c지역 타입을 필드로 설정한 후에 사용하세요.")
            return
        }

        val block: Block? = if(p.gameMode == GameMode.ADVENTURE) p.getTargetBlock(5) else e.clickedBlock
        e.isCancelled = true
        if(block == null) return

        val abilityObject: AbilityObject?

        val type: AbilityType = when(tool.lore!![0]) {
            "§a채집" -> AbilityType.FARM
            "§7채굴" -> AbilityType.MINE
            "§c사냥" -> AbilityType.HUNT
            "§9낚시" -> AbilityType.FISH
            else -> return
        }
        p.sendMessage(tool.lore!![0])
        when(type) {
            AbilityType.FISH -> {
                abilityObject = AbilityBlockManager.getAbilityBlock(block.biome, region.key)
                if(abilityObject == null) {
                    if(AbilityBlockManager.registerAbilityBlock(block.biome, AbilityType.FISH, region.key)) p.sendMessage("§a등록 됨")
                    else p.sendMessage("§c등록 실패")
                } else FishGUI(p, abilityObject as FishObject)
            }
            AbilityType.FARM -> {
                abilityObject = AbilityBlockManager.getAbilityBlock(block.blockData)
                if(abilityObject == null) {
                    if(AbilityBlockManager.registerAbilityBlock(block.blockData.toString(), AbilityType.FARM, region.key)) p.sendMessage("§a등록 됨")
                    else p.sendMessage("§c등록 실패")
                } else FarmGUI(p, abilityObject as FarmObject)
            }
            AbilityType.MINE -> {
                abilityObject = AbilityBlockManager.getAbilityBlock(block.blockData)
                if(abilityObject == null) {
                    if(AbilityBlockManager.registerAbilityBlock(block.blockData.toString(), AbilityType.MINE, region.key)) p.sendMessage("§a등록 됨")
                    else p.sendMessage("§c등록 실패")
                } else MineGUI(p, abilityObject as MineObject)
            }
        }
    }

    @EventHandler
    fun onClickMob(e: PlayerInteractAtEntityEvent) {
        val p = e.player
        val hand = e.hand
        if (!p.isOp || hand != EquipmentSlot.HAND || p.isSneaking) return
        val item = p.inventory.itemInMainHand
        if (!AbilityBlockStick.isStick(item)) return
        val entity = e.rightClicked
        val mob = MythicBukkit.inst().apiHelper.getMythicMobInstance(entity) ?: return
        e.isCancelled = true
        p.gameMode = GameMode.CREATIVE
        val ab = AbilityBlockManager.getAbilityBlock(mob.mobType)
        if (ab != null) {
            HuntGUI(p, ab as HuntObject)
        } else {
            if (AbilityBlockManager.registerAbilityBlock(mob.mobType, AbilityType.HUNT, -1)) {
                p.sendMessage("등록함")
            } else p.sendMessage("§c등록 실패.")
        }
    }
}