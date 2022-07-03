package kr.dreamstory.ability.ability.play.data

import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.extension.fromJson
import com.dreamstory.ability.extension.toJson
import com.dreamstory.ability.manager.MysqlManager
import org.bukkit.entity.Player

data class Friend(
    val owner: Int,
    private val friends: HashSet<Int> = HashSet()
) {

    companion object {
        fun getFriend(id: Int): Friend? = MysqlManager.executeQuery<String>("friend","friends","player",id)?.fromJson()
    }

    private fun tempAdd(target: Int) { friends.add(target) }
    private fun tempRemove(target: Int) { friends.remove(target) }
    private fun update() { MysqlManager.executeQuery("UPDATE friend SET friends='${toJson()}' WHERE player=$owner") }

    fun remove(target: Int): Boolean {
        if(!friends.contains(target)) return false
        friends.remove(target)
        val targetTable: Friend? = getFriend(target)
        targetTable?.tempRemove(owner)

        update()
        targetTable?.update()
        return true
    }

    fun add(target: Int): Boolean {
        if(friends.contains(target)) return false
        var targetTable: Friend? = getFriend(target)
        if(targetTable == null) {
            targetTable = Friend(target)
            targetTable.tempAdd(owner)
            MysqlManager.executeQuery("INSERT INTO friend (player,friends) values ($target,'${targetTable.toJson()}')")
        } else {
            targetTable.tempAdd(owner)
            targetTable.update()
        }
        friends.add(target)
        update()
        return true
    }

    fun sendMessage(p: Player, message: String) { forEach { DSCoreAPI.sendBungeeMessage(p,it,message) } }
    fun contains(target: Int): Boolean = friends.contains(target)
    fun forEach(loop: (Int)->Unit) { for(i in friends) loop(i) }
    fun forEachIndexed(loop: (Int,Int)->Unit) {
        var index = 0
        for(i in friends) loop(index++, i)
    }
    val size: Int get() = friends.size

}

