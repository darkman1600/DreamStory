package com.dreamstory.ability.core.sub

import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import kr.dreamstory.ability.core.main.DSAbility
import com.dreamstory.ability.java.worldguard.WorldGuardSupport
import com.dreamstory.ability.java.worldguard.events.PlayerMoveListener
import com.dreamstory.ability.manager.AbilityBlockManager
import com.dreamstory.ability.manager.RegionManager
import org.bukkit.entity.Player

class ChannelCore(
    core: DSAbility
): Core(core) {

    lateinit var worldGuardSupport: WorldGuardSupport

    override fun onEnable() {
        AbilityBlockManager.load()
        worldGuardSupport = WorldGuardSupport.getInstance(this)
        RegionManager.loadDataTask()
        registerEvents(
            PlayerMoveListener() // supprt
        )
    }

    override fun onDisable() {
        try { BreakAbleBlock.resetCoolTimer() } catch (e: Exception) {}
        AbilityBlockManager.save()
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
    }

}