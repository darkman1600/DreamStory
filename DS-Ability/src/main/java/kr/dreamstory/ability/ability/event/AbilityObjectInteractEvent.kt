package kr.dreamstory.ability.ability.event

import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.AbilityObject
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.potion.PotionEffectType
import java.math.BigDecimal

class AbilityObjectInteractEvent(
    who: Player,
    val abilityObject: AbilityObject,
    val ability: Ability,
    val type: AbilityType,
    val block: Block
) : PlayerEvent(who), Cancellable {

    var breakPower = 1.0
    val plusBigDecimal: BigDecimal
        get() = BigDecimal(breakPower)

    init {
        when(type) {
            AbilityType.MINE -> {
                val buffCheck = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE)
                if(buffCheck?.duration?: 0 >= 2300 && buffCheck?.amplifier?: 0 == 39) breakPower = 10.0
                else {
                    val level = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE)?.amplifier ?: -1
                    breakPower += (level + 1) * 0.05
                }
            }
            AbilityType.FARM -> {
                val buffCheck = player.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)
                if(buffCheck?.duration?: 0 >= 2300 && buffCheck?.amplifier?: 0 == 39) breakPower = 10.0
                else {
                    val level = player.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)?.amplifier ?: -1
                    breakPower += (level + 1) * 0.05
                }
            }
        }
    }

    private var isCancelled = false
    override fun isCancelled(): Boolean { return isCancelled }
    override fun setCancelled(b: Boolean) { isCancelled = b }
    override fun getHandlers(): HandlerList { return HANDLERLIST }

    companion object {
        var HANDLERLIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERLIST
        }
    }
}