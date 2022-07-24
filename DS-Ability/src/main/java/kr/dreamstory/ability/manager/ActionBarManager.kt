package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.extension.ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID

object ActionBarManager {

    val actionBarSet = HashSet<Player>()

    fun run(waitTime: Long) {
        val server = main.server
        val sc = server.scheduler

        sc.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(waitTime)
            while(true) {
                actionBarSet.forEach {
                    it.sendActionBar(Component.text(it.ability!!.actionBar))
                }
                waitFor(1)
            }
        }
    }

}