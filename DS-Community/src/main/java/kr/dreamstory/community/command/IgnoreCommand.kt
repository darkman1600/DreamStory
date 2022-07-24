package kr.dreamstory.community.command

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.ignore.IgnoreManager
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.utils.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class IgnoreCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        val list = ArrayList<String>()
        if(sender !is Player) {
            MessageManager.playerOnlyCmd(sender)
            return list
        }
        when(args.size) {
            1 -> return StringUtil.copyPartialMatches(args[0],listOf("추가","해제"),list)
        }
        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            MessageManager.playerOnlyCmd(sender)
            return false
        }
        if(args.isEmpty()) {
            sender.sendMessage("명령어 도움말 표시")
            return false
        }
        when(args[0]) {
            "추가" -> {
                if(args.size != 2) { sender.sendMessage("잘못된 명령어입니다.\n/$label 추가 <닉네임>"); return false }
                IgnoreManager.addIgnore(sender,args[1])
            }
            "해제" -> {
                if(args.size != 2) { sender.sendMessage("잘못된 명령어입니다.\n/$label 해제 <닉네임>"); return false }
                IgnoreManager.removeIgnore(sender,args[1])
            }
        }
        return true
    }
}