package com.dreamstory.ability.socket.obj

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text

@Deprecated(level = DeprecationLevel.WARNING,message = "클릭 cmd 앞에 ClickEvent.Action 필요")
data class SocketComponent(val mainMsg:String,val hover: String="",val clickCmd: String="") {


    fun build(): TextComponent {

        val com = TextComponent()
        val tx = TextComponent(*TextComponent.fromLegacyText(mainMsg))
        com.addExtra(tx)
        /*for (bs in tx.extra) {
            lastColor = bs.color.toString()
        }*/
        if(hover != "") com.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hover))
        if(clickCmd != "") {
            val args = clickCmd.split(" : ")
            com.clickEvent = ClickEvent(ClickEvent.Action.valueOf(args[0]), args[1])
        }
        return com
    }

}