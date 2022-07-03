package kr.dreamstory.ability.ability.play.skills

import kr.dreamstory.ability.ability.event.AbilityBlockBreakEvent
import org.bukkit.inventory.ItemStack

abstract class Skill(val maxLevel: Int) {

    companion object {
        var lastIndex = 0;
    }

    val skillIndex: Int = lastIndex++

    abstract fun getName(level: Int): String
    abstract fun getSimpleName(level: Int): String
    abstract fun getIcon(level: Int,command: Int): ItemStack
    fun abilityBlockBreakEvent(e: AbilityBlockBreakEvent, level: Int) { e.action(level) }
    open fun AbilityBlockBreakEvent.action(level: Int) {}
}