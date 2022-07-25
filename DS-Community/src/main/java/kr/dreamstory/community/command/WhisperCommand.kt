package kr.dreamstory.community.command

import io.sentry.protocol.Message
import kr.dreamstory.community.main
import kr.dreamstory.library.utils.message.MessageManager
import kr.dreamstory.library.utils.message.MessageType
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class WhisperCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            MessageManager.playerOnlyCmd(sender)
            return false
        }
        if(args.isEmpty()) {
            MessageManager.systemMessage(sender,MessageType.WARNING,"§e/$label <닉네임> <내용> - §f플레이어에게 귓속말을 보냅니다.")
            return false
        }
        if(args.size != 2) {
            MessageManager.systemMessage(sender,MessageType.WARNING,"잘못된 명령어입니다.\n§e/$label <닉네임> <내용> - §f플레이어에게 귓속말을 보냅니다.")
            return false
        }

    }
}