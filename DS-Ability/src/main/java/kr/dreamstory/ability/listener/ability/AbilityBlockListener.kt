package com.dreamstory.ability.listener.ability

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.block.AbilityObject
import kr.dreamstory.ability.ability.play.block.FishObject
import kr.dreamstory.ability.ability.play.block.HuntObject
import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import kr.dreamstory.ability.ability.play.block.obj.Fish
import kr.dreamstory.ability.ability.play.bossbar.BossBarManager
import com.dreamstory.ability.extension.region
import com.dreamstory.ability.listener.interfaces.ChannelListener
import com.dreamstory.ability.manager.AbilityBlockManager
import com.dreamstory.ability.manager.CommandManager
import com.dreamstory.library.coroutine.schedule
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.core.mobs.ActiveMob
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable

class 
AbilityBlockListener: ChannelListener {

    private val mythic by lazy { MythicBukkit.inst().apiHelper }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun defaultCancel(e: BlockBreakEvent) { if(e.player.gameMode != GameMode.CREATIVE) e.isCancelled = true }

    @EventHandler
    fun onDa(e: EntityDamageByEntityEvent) {
        //val damager = e.damager as? Player ?: return
        val p = e.damager as Player
        if (CommandManager.check(p)) {
            e.isCancelled = true
            e.damage = 0.0
            return
        }
        val tool = p.inventory.itemInMainHand
        if (p.getCooldown(tool.type) > 0) {
            e.isCancelled = true
            e.damage = 0.0
            return
        }
        val target = e.entity

        val mob: ActiveMob = mythic.getMythicMobInstance(target) ?: return
        val key = mob.mobType
        val ab = AbilityBlockManager.getAbilityBlock(key)
        if(ab == null) {
            e.isCancelled = true
            e.damage = 0.0
            return
        }
        p.setCooldown(tool.type, 25)
        val le = target as LivingEntity
        BossBarManager.attack(p, le)
        val prevHealth = le.health
        main.server.scheduler.schedule(main) {
            waitFor(1)
            (ab as HuntObject).action(p, le, prevHealth, e.damage)
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onActionBlock(e: PlayerInteractEvent) {
        if(CommandManager.check(e.player)) return
        val p = e.player
        if(p.gameMode != GameMode.ADVENTURE) return
        val hand = e.hand
        if(hand == null || hand != EquipmentSlot.HAND) return
        val action = e.action
        if(action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return
        val block = p.getTargetBlock(5)
        if(block == null || block.type == Material.AIR) return

        val tool = p.inventory.itemInMainHand
        val blockData = block.blockData
        val abilityBlock = AbilityBlockManager.getAbilityBlock(blockData)?: return
        AbilityBlockManager.action(abilityBlock, p, block, tool)

    }

    @EventHandler(priority = EventPriority.LOW)
    fun onFish(e: PlayerFishEvent) {
        if (e.state == PlayerFishEvent.State.CAUGHT_FISH || e.state == PlayerFishEvent.State.CAUGHT_ENTITY) { if (e.caught != null) e.caught?.remove() }

        if (CommandManager.check(e.player)) return
        val p = e.player

        val fish = Fish.getFish(p.uniqueId)

        if (fish != null) {
            e.isCancelled = true
            fish.addPower(p, e.hook, 0.0)
            return
        }
        if (e.state == PlayerFishEvent.State.REEL_IN) {
            val hook = e.hook
            if(hook.passenger != null) hook.passenger!!.remove()
            if(p.hasCooldown(Material.BAT_SPAWN_EGG)) p.setCooldown(Material.BAT_SPAWN_EGG, 0)
        }
        if (e.state == PlayerFishEvent.State.IN_GROUND) {
            val hook = e.hook
            if(hook.passenger != null) hook.passenger!!.remove()
            if(p.hasCooldown(Material.BAT_SPAWN_EGG)) p.setCooldown(Material.BAT_SPAWN_EGG, 0)
        }
        if (e.state == PlayerFishEvent.State.FISHING) {
            if(!p.hasCooldown(Material.BAT_SPAWN_EGG)) p.setCooldown(Material.BAT_SPAWN_EGG, 100000000)
            if(!BreakAbleBlock.breakPlayers.contains(p.uniqueId)) {
                BreakAbleBlock.breakPlayers.add(p.uniqueId)
                val hook = e.hook
                object : BukkitRunnable() {
                    override fun run() {
                        if (hook.isDead || hook.isEmpty) {
                            BreakAbleBlock.breakPlayers.remove(p.uniqueId)
                            cancel()
                        }
                    }
                }.runTaskTimerAsynchronously(main, 0, 1)
            }
        }

        try {
            if (e.state == PlayerFishEvent.State.BITE) {
                e.isCancelled = true
                val biome = e.hook.location.block.biome
                val region = p.region?: return
                val id: Int = region.key
                if (id <= 0) return
                val ab: AbilityObject = AbilityBlockManager.getAbilityBlock(biome, id)?: return
                (ab as FishObject).catchFish(p, p.inventory.itemInMainHand, e.hook)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}