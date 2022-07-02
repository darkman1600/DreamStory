package kr.dreamstory.ability.ability.play.skills

import org.bukkit.Material
import org.bukkit.entity.Player

interface Active {

    fun getCoolTime(level: Int): Double
    fun action(level: Int, p: Player): Boolean
    fun getCoolTimeIcon(): Material

}