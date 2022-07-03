package kr.dreamstory.ability.ability.play.boxs

import com.dreamstory.ability.manager.ItemListManager
import com.dreamstory.ability.manager.MysqlManager
import java.util.*

class PostBox(playerId: Int) {

    companion object {
        fun getPostBox(playerId: Int): PostBox? = if(check(playerId)) PostBox(playerId) else null

        private fun check(playerId: Int): Boolean {
            val id: Int? = MysqlManager.executeQuery("postbox", "id", "player", playerId)
            return id != null
        }
    }

    val owner: Int = playerId

    val size: Int get() = postList.size

    val postList: ArrayList<GiftBoxItem>
        get() {
            val result = ArrayList<GiftBoxItem>()
            val map = MysqlManager.executeQuery("giftbox","player", owner)
            if(map.isNotEmpty()) {
                result.add(
                    GiftBoxItem(
                        map["id"] as Int,
                        map["sender"] as Int,
                        ItemListManager.itemStackArrayFromBase64(map["contents"] as String),
                        Date(map["date"] as Long)
                    )
                )
            }
            return result
        }
}