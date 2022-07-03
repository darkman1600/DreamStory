package kr.dreamstory.ability.ability.event

import kr.dreamstory.ability.ability.play.region.Region
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class RegionEnterEvent (
    who: Player,
    val to: Region,
) : PlayerEvent(who) {

    override fun getHandlers(): HandlerList { return HANDLERLIST }

    companion object {
        var HANDLERLIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERLIST
        }
    }
}