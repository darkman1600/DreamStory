package kr.dreamstory.ability.ability.play.boxs

import com.dreamstory.ability.manager.ItemListManager
import com.dreamstory.ability.manager.MysqlManager
import java.util.*

class GiftBox(playerId: Int) {

    companion object {
        fun getGiftBox(playerId: Int): GiftBox? = if(check(playerId)) GiftBox(playerId) else null

        private fun check(playerId: Int): Boolean {
            //"SELECT id FROM giftbox WHERE player=$playerId"
            val id: Int? = MysqlManager.executeQuery("giftbox", "id", "player", playerId)
            return id != null
        }
    }

    val owner: Int = playerId

    val size: Int get() = giftList.size

    val giftList: ArrayList<GiftBoxItem>
        get() {
            val result = ArrayList<GiftBoxItem>()
            val map = MysqlManager.executeQuery("giftbox","player", owner)
            if(map.isNotEmpty()) {
                result.add(
                    GiftBoxItem(
                        map["id"] as Int,
                        map["sender"] as Int,
                        ItemListManager.itemStackArrayFromBase64(map["contents"] as String),
                        Date((map["date"] as String).toLong())
                    )
                )
            }
            return result
        }

}