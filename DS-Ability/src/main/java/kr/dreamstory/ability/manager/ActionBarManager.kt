package kr.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.extension.ability
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import net.kyori.adventure.text.Component
import org.apache.commons.io.FileUtils.waitFor
import org.bukkit.entity.Player
import java.util.UUID

object ActionBarManager {

    val actionBarSet = HashSet<Player>()

    fun run(waitTime: Long) {
        main.schedule(SynchronizationContext.ASYNC) {
            waitFor(waitTime)
            while(true) {
                for(player in actionBarSet) {
                    player.sendActionBar(Component.text(player.ability!!.actionBar))
                }
                waitFor(1)
            }
        }
    }

}