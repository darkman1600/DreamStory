package com.dreamstory.ability.island

import org.bukkit.entity.Player

@Deprecated(message = "추가 금지", level = DeprecationLevel.WARNING)
object IslandCommand {

    fun onCommand(p: Player, args: Array<String>) {

    }

    fun onTabComplete(p: Player, args: Array<String>): ArrayList<String> {
        return arrayListOf()
    }

}