package kr.dreamstory.library.permission

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.main
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class PermissionCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        TODO("Not yet implemented")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) return false
        main.schedule(SynchronizationContext.ASYNC) {
            val grade = PermissionGrade.fromString(args[1].toUpperCase())
            if(grade == null) {
                sender.sendMessage("등급이 올바르지 않습니다.")
                return@schedule
            }
            val offPlayer = Bukkit.getOfflinePlayer(args[0]).player
            if(offPlayer == null) {
                sender.sendMessage("해당 플레이어를 찾을 수 없습니다.")
                return@schedule
            }
            PermissionManager.setGrade(offPlayer.uniqueId,grade)
        }
        return true
    }
}