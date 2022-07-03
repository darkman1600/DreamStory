package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.island.DSIsland
import kr.dreamstory.ability.ability.play.island.data.DSIslandOption
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.extension.locationToDSLocationString
import com.dreamstory.library.coroutine.schedule
import org.bukkit.Location
import org.bukkit.entity.Player
import world.bentobox.bentobox.BentoBox

object DSIslandManager {

    fun getDSIsland(id: Int, isPlayer: Boolean): DSIsland? {
        val result: Int? = if(!isPlayer) MysqlManager.executeQuery("island", "id", "id", id)
            else MysqlManager.executeQuery("player", "island", "id", id)
        return if(result == null || result == 0) null
        else DSIsland(result)
    }

    fun getIslandNextMakeServer(): String? {
        val server: String? =
            MysqlManager.executeQuery("server", "name", "type", "ISLAND", "order by status asc limit 1")
        return if(server.isNullOrBlank()) null
        else server
    }

    fun registerIsland(p: Player,spawnLocation: Location,islandUniqueId: String) {
        MysqlManager.executeQuery(
            "INSERT INTO island (owner,sub_owners,icon,exp,name,spawn,warp,uuid,challenge,owner_name,sub_owner_name,member_name,point,member_size) values ("
                    + "${p.id},"
                    + "'', "
                    + "'', "
                    + "0, "
                    + "'§e${p.customName} 님의 섬', "
                    + "'${locationToDSLocationString(spawnLocation)}', "
                    + "'${locationToDSLocationString(null)}', "
                    + "'$islandUniqueId', "
                    + "3, "
                    + "'섬장', "
                    + "'부섬장', "
                    + "'섬원', "
                    + "0, "
                    + "20)"
        )
        main.server.scheduler.schedule(main) {
            waitFor(1)
            val id: Int = MysqlManager.executeQuery("island", "id", "owner", p.id)!!
            MysqlManager.executeQuery("UPDATE player SET island=$id WHERE id=${p.id}")
            MysqlManager.executeQuery("UPDATE server SET status=${BentoBox.getInstance().islands.islandCount} WHERE port=${ChannelManager.port}")
            DSIslandOption(id).make()
        }
    }

}