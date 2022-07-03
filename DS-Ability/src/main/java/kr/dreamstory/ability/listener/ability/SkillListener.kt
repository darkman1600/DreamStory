package com.dreamstory.ability.listener.ability

import kr.dreamstory.ability.ability.event.AbilityBlockBreakEvent
import kr.dreamstory.ability.ability.play.skills.Passive
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.listener.interfaces.ChannelListener
import com.dreamstory.ability.manager.SkillManager
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class SkillListener: ChannelListener {

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player
        val id = p.id

        //
    }

    @EventHandler
    fun onAbilityBlockBreak(e: AbilityBlockBreakEvent) {
        val tree = e.ability.getSkillTree(e.type)
        val esList = tree.getLearnSkillsAtEnumSkillList()
        esList.forEach {
            val skill = SkillManager.getSkill(it)
            if(skill is Passive) skill.abilityBlockBreakEvent(e,tree.getSkillLevel(it))
        }
    }

}