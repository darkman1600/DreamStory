package kr.dreamstory.ability.ability.play.skills.cool

import org.bukkit.Material
import org.bukkit.entity.Player

data class CoolTimeData( val playerIdInt: Int ) {
    var coolTimes: HashMap<Material, Int>? = null

    fun apply( p: Player ) { coolTimes?.forEach { (k,v) -> p.setCooldown(k,v) } }
}