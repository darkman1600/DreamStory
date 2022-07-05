package kr.dreamstory.ability.core.sub

import com.dreamstory.ability.core.sub.Core
import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import kr.dreamstory.ability.core.main.DSAbility
import kr.dreamstory.ability.java.worldguard.WorldGuardSupport
import kr.dreamstory.ability.java.worldguard.events.PlayerMoveListener
import kr.dreamstory.ability.manager.AbilityBlockManager
import kr.dreamstory.ability.manager.RegionManager
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