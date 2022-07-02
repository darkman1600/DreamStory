package kr.dreamstory.ability.ability.play.damageskin

import kr.dreamstory.ability.ability.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.Location
import org.bukkit.entity.ArmorStand

object DamageSkinManager {
    fun show(damageFormat: String, location: Location) {
        main.server.scheduler.schedule(main, SynchronizationContext.SYNC) {
            val armorStand = location.world.spawn(location,ArmorStand::class.java) {
                it.isInvisible = true
                it.customName = "- $damageFormat"
                it.isInvulnerable = true
                it.isMarker = true
            }
            //fun random() = (-10..10).random().toDouble() / 100
            //armorStand.velocity = Vector(random(),.2,random())
            waitFor(2)
            armorStand.isCustomNameVisible = true
            for(i in 1..5) {
                armorStand.teleport(location.add(.0,.125,.0))
                waitFor(1)
            }
            waitFor(5)
            armorStand.remove()
        }
    }
}