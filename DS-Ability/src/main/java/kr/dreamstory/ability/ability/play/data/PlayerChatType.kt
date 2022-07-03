package kr.dreamstory.ability.ability.play.data

import net.md_5.bungee.api.ChatColor
import java.awt.Color

enum class PlayerChatType(val label: String,private val color: Color,val index: Int) {

    ALL("§7전체채팅",Color(179,179,179),0),
    AREA("§6지역채팅",Color(193,135,35),1),
    ISLAND("§a섬채팅",Color(122,189,82),2),
    FRIEND("§b친구채팅",Color(45,160,160),3),
    OP("§c관리자채팅팅",Color(219,77,20),4);

    fun next(isOp: Boolean): PlayerChatType = indexOf(index+1, isOp)
    val chatColor: ChatColor get() = ChatColor.of(color)

    companion object {
        fun indexOf(index: Int,isOp: Boolean): PlayerChatType {
            return when(index) {
                0-> ALL
                1-> AREA
                2-> ISLAND
                3-> FRIEND
                4-> {
                    if(isOp) OP
                    else ALL
                }
                else-> ALL
            }
        }
    }

}