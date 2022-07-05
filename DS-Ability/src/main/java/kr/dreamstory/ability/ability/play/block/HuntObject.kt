package kr.dreamstory.ability.ability.play.block

import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.obj.DamageInfo
import kr.dreamstory.ability.ability.play.block.obj.MobType
import kr.dreamstory.ability.extension.ability
import com.dreamstory.ability.extension.naturalDrop
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.math.RoundingMode

class HuntObject(
    type: AbilityType,
    key: String,
    regionName: String,
    prevLevel: Int,
    maxLevel: Double,
    exp: Long,
    drop: ItemStack?,
    val data: String?
): AbilityObject(
    type,
    key,
    regionName,
    prevLevel,
    maxLevel,
    exp,
    drop?: ItemStack(Material.STONE),
    data
) {

    var mobName: String
        private set
    var mobType: MobType
        private set
    var dungeonName: String = ""
    var bossN: Int = 1

    fun setType() { mobType = mobType.next() }

    init {
        mobName = key
        mobType = MobType.MOB
        dungeonName = ""
        bossN = 1
        if(data != null) load()
    }

    override fun load() {
        val args = data!!.split(" : ")
        mobName = args[0]
        mobType = MobType.valueOf(args[1])
        dungeonName = args[2]
        bossN = args[3].toInt()
    }
    override fun toDataString(): String = "$mobName : $mobType : $dungeonName : $bossN"

    fun action(p: Player,le: LivingEntity, prevHealth: Double,damage: Double) {
        if(mobType == MobType.BOSS1 || mobType == MobType.BOSS2 || damage <= 0 || prevHealth <= 0) return

        val currentHealth = le.health
        var death = false
        var lastDamage = damage
        if(currentHealth <= 0) {
            if(prevHealth < damage) lastDamage = prevHealth
            death = true
        }

        val damageInfo = DamageInfo.getDamageInfo(le)
        damageInfo.addDamage(lastDamage, p.uniqueId)
        if(death) {
            if(mobType == MobType.MOB) {
                dropItem.naturalDrop(p)
            }
            damageInfo.ranks.forEach {
                val p = server.getPlayer(it)?: return@forEach
                val ab = p.ability?: return@forEach
                val percent = BigDecimal(damageInfo.getDamagePercent(le.maxHealth, it)).divide(BigDecimal(100), 3, RoundingMode.DOWN)
                val addExp = BigDecimal(exp).multiply(percent)
                ab.addExp(addExp.toLong(), AbilityType.HUNT)
                server.broadcastMessage("${p.name} : ${addExp.toLong()} [ damage : ${damageInfo.getDamage(it)}]")
            }
            damageInfo.remove = true
        }
    }

}