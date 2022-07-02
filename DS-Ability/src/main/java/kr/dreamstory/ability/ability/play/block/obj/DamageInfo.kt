package kr.dreamstory.ability.ability.play.block.obj

import kr.dreamstory.ability.ability.main
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.entity.LivingEntity
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class DamageInfo(entity: LivingEntity) {

    companion object {
        val server = main.server
        val scheduler by lazy { server.scheduler }
        val damageMap = HashMap<UUID, DamageInfo>() // k = Living Entity , v = DamageInfo Instance
        fun getDamageInfo(entity: LivingEntity): DamageInfo = damageMap[entity.uniqueId]?: DamageInfo(entity)
    }

    private val maxHealth: BigDecimal
    private val entityUniqueId: UUID = entity.uniqueId
    private val damageSet: HashMap<UUID, DamagerData> // k = Damager Entity UniqueId , v = DamagerData(BigDecimal[damage], Task Tick)
    var remove = false

    init {
        damageMap[entityUniqueId] = this
        maxHealth = BigDecimal(entity.maxHealth)
        damageSet = HashMap()
        runTask()
    }

    private fun runTask() {
        scheduler.schedule(main, SynchronizationContext.ASYNC) {
            repeating(2)
            while(!remove) {
                val removeUUID = HashSet<UUID>()
                damageSet.forEach { (key,value)-> if(--value.tick <= 0) removeUUID.add(key) }
                if(removeUUID.isNotEmpty()) removeUUID.forEach { damageSet.remove(it) }
                if(damageSet.isEmpty()) break
                yield()
            }
            damageMap.remove(entityUniqueId)
        }
    }

    fun addDamage(damage: Double, damagerUniqueId: UUID) {
        val damageData = damageSet[damagerUniqueId]
        val damageBigDecimal = BigDecimal(damage)
        if(damageData == null) {
            val temp = DamagerData(damageBigDecimal, 1000)
            damageSet[damagerUniqueId] = temp
        } else {
            damageData.tick = 1000
            damageData.damage = damageData.damage.add(damageBigDecimal)
        }
    }

    val ranks: List<UUID> get() {
        val rank:List<UUID> = damageSet.keys.toList()
        rank.sortedByDescending { uuid -> damageSet[uuid]?.damage?.toDouble()?: 0.0 }
        return rank
    }

    fun getDamage(damagerUniqueId: UUID): Double = damageSet[damagerUniqueId]?.damage?.toDouble()?: 0.0

    fun getDamagePercent(maxHealth: Double,damagerUniqueId: UUID): Double {
        var damagerData = damageSet[damagerUniqueId]?: return 0.0
        var result = BigDecimal(maxHealth)
        result = damagerData.damage.divide(result, 3, RoundingMode.DOWN)
        return result.toDouble() * 100.0
    }

    data class DamagerData(var damage: BigDecimal, var tick: Int)

}