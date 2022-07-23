package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.extension.ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object ActionBarManager {

    fun run(waitTime: Long) {
        val server = main.server
        val sc = server.scheduler

        sc.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(waitTime)
            while(true) {
                server.onlinePlayers.forEach {
                    val ability = it.ability
                    if(ability?.actionBarToggle == true) {
                        it.sendActionBar(Component.text(ability.actionBar))
                    }
                }
                waitFor(1)
            }
        }
    }

}