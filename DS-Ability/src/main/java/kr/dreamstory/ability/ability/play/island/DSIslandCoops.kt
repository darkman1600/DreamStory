package kr.dreamstory.ability.ability.play.island

import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.manager.MysqlManager
import org.bukkit.entity.Player
import java.util.*

class DSIslandCoops(private val island: Int, private val members: HashSet<Int> = HashSet()) {

    val size: Int get() = members.size

    fun add(playerId: Int): Boolean {
        if(members.contains(playerId)) return false
        members.add(playerId)
        MysqlManager.executeQuery("INSERT INTO island_coop (player,island,data) value ($playerId,$island,${Date().time})")
        return true
    }

    fun remove(playerId: Int): Boolean {
        if(!members.contains(playerId)) return false
        members.remove(playerId)
        MysqlManager.executeQuery("DELETE FROM island_coop WHERE player=$playerId")
        return true
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "remove all players")
    fun drop() {
        members.clear()
        MysqlManager.executeQuery("DELETE FROM island_coop WHERE island=$island")
    }

    fun contains(playerId: Int): Boolean = members.contains(playerId)

    fun forEach(loop: (Int)->Unit) { for(i in members) loop(i) }
    fun forEachIndexed(loop: (Int,Int) -> Unit) {
        var index = 0
        for(i in members) loop(index++, i)
    }
    fun sendMessage(p: Player, message:String) { forEach { DSCoreAPI.sendBungeeMessage(p, it, message) } }

}