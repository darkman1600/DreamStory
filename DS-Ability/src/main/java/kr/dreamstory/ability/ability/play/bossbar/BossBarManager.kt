package kr.dreamstory.ability.ability.play.bossbar

import kr.dreamstory.ability.ability.main
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.util.*

object BossBarManager {

    private val bossBarMap = HashMap<UUID, DSBossBar>()
    val scheduler by lazy { main.server.scheduler }
    val healthFormat = DecimalFormat("###,###.#")

    fun attack(p: Player, le: LivingEntity) {
        val uuid = le.uniqueId
        scheduler.schedule(main) {
            waitFor(1)
            val wfb: DSBossBar
            = if(bossBarMap.containsKey(uuid)) bossBarMap[uuid]!! else {
                val temp = DSBossBar(uuid, le.customName?: le.name, le.maxHealth)
                bossBarMap[uuid] = temp
                temp
            }

            wfb.addPlayer(p)
            if(le.isDead || le.health <= 0) {
                wfb.setHealth(0.0)
                bossBarMap.remove(uuid)
            } else wfb.setHealth(le.health)
        }
    }

}