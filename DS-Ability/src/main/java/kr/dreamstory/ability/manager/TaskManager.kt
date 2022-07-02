package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.extension.ability
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.extension.updateSQL
import com.dreamstory.ability.extension.updateSql
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
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
                        val actionBar = it.ability?.actionBar ?: return@forEach
                        it.sendActionBar(Component.text(actionBar))
                    } catch (e: Exception) { return@forEach }
                }
                waitFor(1)
            }
        }

        sc.schedule(main, SynchronizationContext.ASYNC) {
            repeating(6000)
            while(true) {
                server.onlinePlayers.forEach { p->
                    val id = p.id
                    if(id <= 0) return@forEach
                    p.updateSql()
                    p.ability?.updateSQL()
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