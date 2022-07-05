package kr.dreamstory.ability.ability.play.block.obj

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.extension.ability
import com.dreamstory.ability.extension.naturalDrop
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class Fish internal constructor(
    private val item: ItemStack,
    private val power: Double,
    private val playerPower: Double,
    private val exp: Long
) {
    private var health = 0.0
    private var maxHealth = 0.0
    private val hook: Entity? = null

    companion object {
        private var fishMap: MutableMap<UUID, Fish> = HashMap()
        private const val left = "\u2551"
        private const val right = "\u2552"
        private const val gColor = "\u2553"
        private const val gVoid = "\u2554"
        fun getFish(uuid: UUID): Fish? = fishMap[uuid]
        private val coolSet: MutableSet<UUID> = HashSet()

    }

    fun catchFish(p: Player, maxH: Double, h: Double, hook: Entity) {
        fishMap[p.uniqueId] = this
        maxHealth = maxH
        health = h
        main.server.scheduler.schedule(main) {
            var cnt = 0
            repeating(1)
            while(p.isOnline && !hook.isDead) {
                p.sendTitle("", state, 0, 5, 0)
                if (cnt > Random().nextInt(20) + 10) {
                    cnt = 0
                    health -= power
                    if (health <= 0) {
                        health = 0.0
                        break
                    }
                } else cnt++
                yield()
            }
            fishMap.remove(p.uniqueId)
        }
    }

    fun addPower(p: Player, hook: Entity, addPower: Double): Boolean {
        if (coolSet.contains(p.uniqueId)) return false
        val hookLocation = hook.location
        coolSet.add(p.uniqueId)
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            waitFor(10)
            coolSet.remove(p.uniqueId)
        }
        health += playerPower + addPower
        if (health > maxHealth) health = maxHealth
        if (health == maxHealth) {
            val item = item.clone()
            val result = item.naturalDrop(p)
            val name = if (result.itemMeta.hasDisplayName()) result.itemMeta.displayName else result.i18NDisplayName!!
            val ab = p.ability
            ab?.addExp(exp, AbilityType.FISH)
            val i = hookLocation.world.dropItemNaturally(hookLocation, result)
            hook.remove()
            i.setCanPlayerPickup(false)
            i.customName = name
            i.isCustomNameVisible = true
            main.server.scheduler.schedule(main) {
                waitFor(10)
                i.velocity = Vector()
                i.setGravity(false)
                waitFor(30)
                i.remove()
            }
            return true
        }
        return false
    }

    private val state: String
        get() {
            val per = health / maxHealth
            val res = per * 100
            val a = Math.round(res).toInt()
            val result = StringBuilder()
            result.append(left).append("\uF801")
            for (i in 0..99) {
                result.append("\uF801").append(if (i < a) gColor else gVoid)
            }
            result.append("\uF801").append(right)
            return result.toString()
        }
}