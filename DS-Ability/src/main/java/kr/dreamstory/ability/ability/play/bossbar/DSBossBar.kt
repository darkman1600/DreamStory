package kr.dreamstory.ability.ability.play.bossbar

import kr.dreamstory.ability.ability.main
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import java.util.*

class DSBossBar(
    val entity: UUID,
    val entityName: String,
    val maxHealth: Double
) {

    private val playerTaskMap = HashMap<UUID,Int>()
    private val bossBar by lazy { main.server.createBossBar(title, BarColor.RED, BarStyle.SOLID) }
    private var task:Int = 0
    private var health: Double = maxHealth

    init {
        bossBar.progress = 1.0
    }

    fun addPlayer(p: Player) {
        val uuid = p.uniqueId
        playerTaskMap[uuid] = 200
        if(!bossBar.players.contains(p)) bossBar.addPlayer(p)
        if(!bossBar.isVisible) bossBar.isVisible = true
        run()
    }

    fun setHealth(value: Double): Boolean {
        health = if(value < 0) 0.0 else value
        bossBar.setTitle(title)
        var pro = health / maxHealth
        if(pro < 0) pro = 0.0
        else if(pro > 1) pro = 1.0
        bossBar.progress = pro
        return if(pro <= 0.0) {
            if(task != 0) BossBarManager.scheduler.cancelTask(task)
            BossBarManager.scheduler.runTaskLater(
                main,
                Runnable {
                    playerTaskMap.clear()
                    bossBar.isVisible = false
                    bossBar.removeAll()
                },1L)
            true
        } else false
    }

    private fun run() {
        if(task != 0) return
        val server = main.server
        task = BossBarManager.scheduler.runTaskTimer(main,Runnable {
            val remover = HashSet<UUID>()
            playerTaskMap.forEach { (key,value)->
                val tempPlayer = server.getPlayer(key)
                val temp = if(tempPlayer == null || !tempPlayer.isOnline) { 0 } else value - 1
                if(temp <= 0) remover.add(key)
                else playerTaskMap[key] = value
            }
            remover.forEach {
                val tempPlayer = server.getPlayer(it)
                if(tempPlayer != null) bossBar.removePlayer(tempPlayer)
                playerTaskMap.remove(it)
            }
            if(playerTaskMap.size == 0) {
                val id = task
                task = 0
                bossBar.isVisible = false
                bossBar.removeAll()
                BossBarManager.scheduler.cancelTask(id)
                return@Runnable
            }
        }, 1L, 1L).taskId
    }

    private val title: String get() = "$entityName §a${BossBarManager.healthFormat.format(health)} §f/ §6${BossBarManager.healthFormat.format(maxHealth)}"

}