package kr.dreamstory.ability.ability.play.block

import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.obj.FishBuilder
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class FishObject(
    type: AbilityType,
    key: String,
    regionId: Int,
    prevLevel: Int,
    maxLevel: Double,
    exp: Long,
    drop: ItemStack?,
    val data: String?
): AbilityObject(
    type,
    key,
    regionId,
    prevLevel,
    maxLevel,
    exp,
    drop?: ItemStack(Material.STONE),
    data
) {

    var biome = Biome.valueOf(key.split(" : ")[1])
        private set
    var maxHealth: Double
    var health: Double
    var minPower : Double
    var maxPower : Double

    init {
        maxHealth = 100.0
        health = 20.0
        minPower = 1.0
        maxPower = 1.0
        if(data != null) load()
    }

    override fun load() {
        val args = data!!.split(" : ")
        maxHealth = args[0].toDouble()
        health = args[1].toDouble()
        minPower = args[2].toDouble()
        maxPower = args[3].toDouble()
    }
    override fun toDataString(): String = "$maxHealth : $health : $minPower : $maxPower"

    fun catchFish(p: Player, tool: ItemStack, hook: Entity) {
        val fishBuilder = FishBuilder(dropItem)
        val random = Random
        val rand1 = try { random.nextDouble(minPower, maxPower) } catch (e: Exception) { 1.0 }
        val rand2 = random.nextDouble(health-5.0, health+5.0)
        // 툴 데미지
        fishBuilder.setPower(rand1).setPlayerPower(4.0)
        val fish = fishBuilder.build(exp)
        fish.catchFish(p, maxHealth, rand2, hook)
    }

}