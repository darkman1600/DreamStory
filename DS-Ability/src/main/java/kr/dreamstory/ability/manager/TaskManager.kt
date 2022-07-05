package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.extension.ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import net.kyori.adventure.text.Component

object TaskManager {

    fun run(waitTime: Long) {
        val server = main.server
        val sc = server.scheduler

        sc.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(waitTime)
            while(true) {
                server.onlinePlayers.forEach {
                    try {
                        val actionBar = it.ability.actionBar ?: return@forEach
                        it.sendActionBar(Component.text(actionBar))
                    } catch (e: Exception) { return@forEach }
                }
                waitFor(1)
            }
        }

        sc.schedule(main, SynchronizationContext.ASYNC) {
            repeating(6000)
            while(true) {
                server.onlinePlayers.forEach { p ->
                    p.ability.updateData()
                }
                yield()
            }
        }

        sc.schedule(main, SynchronizationContext.ASYNC) {
            repeating(72000)
            while(true) { LogManager.saveLogAll(server); yield() }
        }
    }

}