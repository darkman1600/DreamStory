package kr.dreamstory.ability.ability.play.superjump

import kr.dreamstory.ability.ability.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

object  SuperJumpManager {
    private val power = HashMap<UUID, Power>()
    private val delay = HashSet<UUID>()
    fun getNewPower(uuid: UUID): Power {
        power[uuid] = Power()
        return power[uuid]!!
    }

    fun jump(player: Player) {
        val uuid = player.uniqueId
        if(power.contains(uuid)) {
            val data = power[uuid]!!
            if(!player.isSneaking) return
            player.isSneaking = false
            val loc = player.location
            val front = loc.clone().add(loc.direction.normalize().multiply(3))
            player.playSound(loc,Sound.ENTITY_ENDER_DRAGON_FLAP,0.4F,2F)
            player.playSound(loc,Sound.ENTITY_ARROW_HIT,.1F,1.2F)
            val color: Color
            if(data.progress >= 80) {
                color = Color.AQUA
                player.playSound(loc,Sound.ENTITY_BLAZE_SHOOT,0.2F,1.5F)
                player.velocity = front.toVector().subtract(loc.toVector()).multiply(if(player.isSprinting) 0.33 else 0.45)
                player.world.spawnParticle(Particle.EXPLOSION_LARGE,player.location,2,.1,.1,.1,.0)
            } else {
                player.velocity = front.toVector().subtract(loc.toVector()).multiply(if(player.isSprinting) 0.14 else 0.24)
                color = Color.WHITE
            }
            data.progress = 0
            data.turn = true

            main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
                while(!player.isOnGround && !player.isFlying) {
                    player.world.spawnParticle(Particle.REDSTONE,player.location,5,.15,.15,.15,Particle.DustOptions(color,1.8F))
                    waitFor(1)
                }
            }
        }
    }
    fun trigger(player: Player) {
        val uuid = player.uniqueId
        val data = getNewPower(uuid)
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            while (player.isOnline && player.isSneaking) {
                if(data.turn) data.progress += 4 else data.progress -= 4
                val progress = data.progress

                if(progress >= 100) {
                    data.turn = false
                }
                else if(progress <= 0) {
                    data.turn = true
                }
                player.sendExperienceChange(progress / 100F)
                waitFor(1)
            }
            power.remove(uuid)
            waitFor(20)
            if(!power.contains(uuid)) {
                player.sendExperienceChange(0F)
            }

        }
    }
}