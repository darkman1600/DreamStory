package kr.dreamstory.ability.ability.play.island.data

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.extension.fromJson
import com.dreamstory.ability.extension.toJson
import com.dreamstory.ability.manager.MysqlManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule

data class DSIslandOption(
    val islandId: Int,
    var blockPlace: Boolean = false,
    var blockBreak: Boolean = false,
    var pvp: Boolean = false,
    var interact: Boolean = false,
    var challenge: IslandOptionType = IslandOptionType.OWNER,
    var invite: IslandOptionType = IslandOptionType.OWNER,
    var coop_invite: IslandOptionType = IslandOptionType.OWNER,
    var kick: IslandOptionType = IslandOptionType.OWNER,
    var expell: IslandOptionType = IslandOptionType.OWNER,
    var icon: IslandOptionType = IslandOptionType.OWNER,
    var board: IslandOptionType = IslandOptionType.OWNER,
    var notice: IslandOptionType = IslandOptionType.OWNER,
    var changed: Boolean = false
) {

    companion object {
        fun getWFIslandOptionById(islandId: Int): DSIslandOption? = MysqlManager.executeQuery<String?>("island_option", "option_data", "island", islandId)?.fromJson()
    }

    fun update() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) { updateData() }
    }

    @Deprecated(level = DeprecationLevel.WARNING,message = "Dont Use")
    fun make() {
        val sql = "INSERT INTO island_option (island, option_data) values ($islandId,'${toJson()}')"
        val s = MysqlManager.connection!!.prepareStatement(sql)
        s.execute()
        s.close()
    }

    private fun updateData() {
        val sql = "UPDATE island_option SET option_data='${toJson()}' WHERE island=$islandId"
        val s = MysqlManager.connection!!.prepareStatement(sql)
        s.execute()
        s.close()
    }

}