package kr.dreamstory.ability.ability.play.item

import org.bukkit.ChatColor

enum class DropGrade(
    val color: ChatColor,
    val label: String,
    val index: Int
) {

    LOW(ChatColor.GRAY,"하급",0),
    NORMAL(ChatColor.WHITE,"일반",1),
    HIGH(ChatColor.GREEN,"고급",2),
    FINEST(ChatColor.AQUA,"최고급",3),
    LEGEND(ChatColor.GOLD,"전설",4)

}