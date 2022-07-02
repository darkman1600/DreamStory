package com.dreamstory.ability.listener.ability

import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import kr.dreamstory.ability.ability.play.command.InputType
import kr.dreamstory.ability.ability.play.region.RegionType
import com.dreamstory.ability.extension.region
import com.dreamstory.ability.listener.interfaces.ChannelListener
import com.dreamstory.ability.manager.CommandManager
import com.dreamstory.ability.manager.CommandManager.commandText
import net.minecraft.world.EnumHand
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.EquipmentSlot

class CommandListener: ChannelListener {

    @EventHandler
    fun onShift(e: PlayerToggleSneakEvent) {
        val p = e.player
        if (e.isCancelled || CommandManager.delayList.contains(p.uniqueId) || !CommandManager.check(p)
            || p.vehicle != null || p.region == null || p.isSneaking) return
        val sc = p.commandText
        if (sc.input(CommandManager.SHIFT, CommandManager.getText(CommandManager.SHIFT))) {
            CommandManager.action(p, sc, p.region!!.regionType)
        } else CommandManager.setDelay(p, 1L)
    }

    @EventHandler
    fun onF(e: PlayerSwapHandItemsEvent) {
        val p = e.player
        if (CommandManager.delayList.contains(p.uniqueId) || !CommandManager.check(p)
            || p.vehicle != null || p.region == null || p.isSneaking) return
        e.isCancelled = true
        val sc = p.commandText
        if(sc.input(CommandManager.F, CommandManager.getText(CommandManager.F))) {
            CommandManager.action(p, sc, p.region!!.regionType)
        } else CommandManager.setDelay(p, 1L)
    }

    @EventHandler
    fun onQ(e: PlayerDropItemEvent) {
        val p = e.player
        if (CommandManager.delayList.contains(p.uniqueId) || !CommandManager.check(p)
            || p.vehicle != null || p.region == null || p.isSneaking) return
        e.isCancelled = true
        val sc = p.commandText
        if(sc.input(CommandManager.Q, CommandManager.getText(CommandManager.Q))) {
            CommandManager.action(p, sc, p.region!!.regionType)
        } else CommandManager.setDelay(p, 1L)
    }

    @EventHandler
    fun onMouseClick(e: PlayerInteractEvent) {
        if(e.hand == null || e.hand != EquipmentSlot.HAND) return
        val p = e.player
        val hand = p.inventory.itemInMainHand
        val type = p.region?.regionType
        if(type == null || type == RegionType.NONE) return

        if(hand.type == Material.FISHING_ROD) {
            e.isCancelled = true
            if(e.action == Action.RIGHT_CLICK_AIR) {
                if(BreakAbleBlock.breakPlayers.contains(p.uniqueId)) {
                    CommandManager.setDelay(p, 1L)
                    return
                }
            }

            if(e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                if(!CommandManager.check(p) && !CommandManager.delayList.contains(p.uniqueId)) {
                    val nmsPlayer = (p as CraftPlayer).handle
                    val nmsHeldItemStack = CraftItemStack.asNMSCopy(hand)
                    nmsHeldItemStack.a()
                    val interactionResultWrapper = nmsHeldItemStack.a(nmsPlayer.s,nmsPlayer,EnumHand.a)
                    interactionResultWrapper.a()
                    CommandManager.setDelay(p, 1L)
                    return
                }
            }
        }

        if(BreakAbleBlock.breakPlayers.contains(p.uniqueId)) return
        if(!CommandManager.itemCheck(p) || CommandManager.delayList.contains(p.uniqueId) || p.vehicle != null) return

        val action:Int
        when(e.action) {
            Action.LEFT_CLICK_BLOCK-> action = CommandManager.LEFT
            Action.LEFT_CLICK_AIR-> action = CommandManager.LEFT
            Action.RIGHT_CLICK_BLOCK-> action = CommandManager.RIGHT
            Action.RIGHT_CLICK_AIR-> action = CommandManager.RIGHT
            else -> action = 0
        }

        if(action == 0) return

        if(CommandManager.check(p)) {
            e.isCancelled = true
            val sc = p.commandText
            if(sc.input(action, CommandManager.getText(action))) {
                CommandManager.action(p, sc, type)
            } else CommandManager.setDelay(p, 1L)
        } else {
            if(action == CommandManager.RIGHT) {
                if(!CommandManager.isAbilityTool(hand.type)) return
                e.isCancelled = true
                CommandManager.put(p, type, InputType.INPUT, CommandManager.RIGHT)
                CommandManager.setDelay(p, 1L)
            }
        }
    }

}