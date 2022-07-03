package kr.dreamstory.ability.ability.play.data

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.extension.fromJson
import com.dreamstory.ability.extension.toJson
import com.dreamstory.ability.manager.MysqlManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule

data class PlayerOption(
    val owner: Int,
    var friendInvite: Boolean = true,
    var islandInvite: Boolean = true,
    var friendJoin: Boolean = true,
    var islandJoin: Boolean = true,
    var whisper: OptionType = OptionType.ALL_ACCEPT,
    var trade: OptionType = OptionType.ALL_ACCEPT,
    var privateInfo: OptionType = OptionType.ALL_ACCEPT,
    var weather: WeatherType = WeatherType.SUN,
    var time: TimeType = TimeType.MORNING,
    var skinNumber: Int = 0,
    var changed: Boolean = false
) {

    companion object {
        fun getPlayerOptionById(playerId: Int): PlayerOption? = MysqlManager.executeQuery<String?>("player_option", "option_data", "player", playerId)?.fromJson()
    }

    fun update() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) { updateData() }
    }

    @Deprecated(level = DeprecationLevel.WARNING,message = "Dont Use")
    fun make() {
        val sql = "INSERT INTO player_option (player, option_data) values ($owner,'${toJson()}')"
        val s = MysqlManager.connection!!.prepareStatement(sql)
        s.execute()
        s.close()
    }

    private fun updateData() {
        val sql = "UPDATE player_option SET option_data='${toJson()}' WHERE player=$owner"
        val s = MysqlManager.connection!!.prepareStatement(sql)
        s.execute()
        s.close()
    }

}
