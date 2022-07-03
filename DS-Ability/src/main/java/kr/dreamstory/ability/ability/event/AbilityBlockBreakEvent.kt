package kr.dreamstory.ability.ability.event

import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.AbilityObject
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import java.math.BigDecimal

class AbilityBlockBreakEvent(
    who: Player,
    val abilityObject: AbilityObject,
    val ability: Ability,
    val type: AbilityType,
    val block: Block,
    val isSkill: Boolean
) : PlayerEvent(who), Cancellable {

    var fatigue = 0
    var dropPercent = 0.0
    val plusBigDecimal: BigDecimal
        get() = BigDecimal(dropPercent)

    override fun getHandlers(): HandlerList { return HANDLERLIST }

    companion object {
        var HANDLERLIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERLIST
        }
    }

    private var isCancelled = false
    override fun isCancelled(): Boolean { return isCancelled }
    override fun setCancelled(boolean: Boolean) { isCancelled = boolean }
}