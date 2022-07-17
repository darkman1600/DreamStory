package kr.dreamstory.community.command

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.chat.ChatMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class ChatModeCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        TODO("Not yet implemented")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            val state = CommunityManager.getState(sender.uniqueId)
            state.setChatMode(ChatMode.fromString(args[0]) ?: ChatMode.DEFAULT)
            sender.sendMessage("현재 채팅모드는 ${state.chatMode.display} 입니다.")
        }
        return true
    }
}