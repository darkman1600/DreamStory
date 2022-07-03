package com.dreamstory.ability.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import kr.dreamstory.ability.ability.play.superjump.SuperJumpManager
import com.dreamstory.ability.manager.CommandManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent

class SuperJumpListener: Listener {
    @EventHandler
    fun onSneaking(event: PlayerToggleSneakEvent) {
        val player = event.player
        if(CommandManager.check(player)) return
        SuperJumpManager.trigger(event.player)
    }

    @EventHandler
    fun onJump(event: PlayerJumpEvent) {
        SuperJumpManager.jump(event.player)
    }
}