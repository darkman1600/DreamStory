package kr.dreamstory.community.command.op

import kr.dreamstory.community.friend.FriendSetupManager
import kr.dreamstory.library.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class FriendOpCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if(sender !is Player) {
            MessageManager.playerOnlyCmd(sender)
            return list
        }
        when(args.size) {
            1 -> return StringUtil.copyPartialMatches(args[0],listOf("설정","삭제"),list)
            2 -> {
                when(args[0]) {
                    "설정" -> {
                        StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().map { it.name },list)
                        list.remove(sender.name)
                    }
                    "삭제" -> {
                        StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().map { it.name },list)
                        list.remove(sender.name)
                    }
                }
            }
            3 -> {
                when(args[0]) {
                    "설정" -> {
                        StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().map { it.name },list)
                        list.remove(sender.name)
                    }
                    "삭제" -> {
                        StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().map { it.name },list)
                        list.remove(sender.name)
                    }
                }
            }
        }
        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            MessageManager.playerOnlyCmd(sender)
            return false
        }
        if(args.isEmpty()) return false
        when(args[0]) {
            "설정" -> {
                if(args.size != 3) { sender.sendMessage("잘못된 명령어입니다.\n/$label 설정 <닉네임> <닉네임>"); return false }
                FriendSetupManager.setFriend(sender,args[1],args[2])
            }
            "삭제" -> {
                if(args.size != 3) { sender.sendMessage("잘못된 명령어입니다.\n/$label 삭제 <닉네임> <닉네임>"); return false }
                FriendSetupManager.removeFriend(sender,args[1],args[2])
            }
        }
        return true
    }
}