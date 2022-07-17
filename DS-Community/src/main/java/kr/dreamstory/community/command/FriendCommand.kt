package kr.dreamstory.community.command

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.friend.FriendManager
import kr.dreamstory.community.gui.FriendGUI
import kr.dreamstory.community.request.RequestManager
import kr.dreamstory.community.request.RequestType
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class FriendCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if(sender !is Player) {
            MessageManager.playerOnlyCmd(sender)
            return list
        }
        when(args.size) {
            1 -> return StringUtil.copyPartialMatches(args[0],listOf("신청","수락","삭제"),list)
            2 -> {
                when(args[0]) {
                    "신청" -> {
                        StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().map { it.name },list)
                        list.remove(sender.name)
                    }
                    "삭제" -> {
                        val friendsNames = CommunityManager.getState(sender.uniqueId).friends.mapNotNull {
                            PlayerDataManger.getOfflinePlayerData(it).name
                        }
                        StringUtil.copyPartialMatches(args[1],friendsNames,list)
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
            "목록" -> FriendGUI(sender).open(sender)
            "신청" -> {
                if(args.size != 2) { sender.sendMessage("잘못된 명령어입니다.\n/$label 신청 <닉네임>"); return false }
                RequestManager.request(RequestType.ADD_FRIEND,sender,args[1])
            }
            "수락" -> {
                if(args.size != 1) { sender.sendMessage("잘못된 명령어입니다.\n/$label 수락"); return false }
                RequestManager.accept(sender,RequestType.ADD_FRIEND)
            }
            "거절" -> {
                if(args.size != 1) { sender.sendMessage("잘못된 명령어입니다.\n/$label 거절"); return false }
                RequestManager.defuse(sender,RequestType.ADD_FRIEND)
            }
            "삭제" -> {
                if(args.size != 2) { sender.sendMessage("잘못된 명령어입니다.\n/$label 거절"); return false }
                FriendManager.remove(sender,args[1])
                return false
            }
        }
        return true
    }
}