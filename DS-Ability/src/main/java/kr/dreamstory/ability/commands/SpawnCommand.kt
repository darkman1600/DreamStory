package kr.dreamstory.ability.commands

import com.dreamstory.ability.extension.changeChannel
import com.dreamstory.ability.manager.ChannelManager
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class SpawnCommand: TabExecutor {
    companion object {
        var spawn: Location? = null
        fun setSpawnPoint(location: Location) { spawn = location }
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String>? {
        TODO("Not yet implemented")
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(sender !is Player) return false
        if(args.isNotEmpty()) {
            if(sender.isOp && args.size == 1 && args[0] == "설정") {
                spawn = sender.location
                sender.sendMessage("스폰 지점을 현재 위치로 설정하였습니다.")
            }
            return true
        }
        if(!ChannelManager.isMainServer) {
            sender.changeChannel("server1")
        } else {
            if(spawn == null) {
                sender.sendMessage("스폰 포인트 미설정. 관리자에게 문의하세요.")
                return false
            }
            sender.teleport(spawn!!)
        }
        return true
    }
}