package kr.dreamstory.library.utils.message

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

object MessageManager {
    fun pluginMessage(plugin: JavaPlugin, message: String) {
        Bukkit.getConsoleSender().sendMessage("§e[${plugin.name}]§r $message")
    }
    fun pluginMessage(plugin: JavaPlugin, sender: CommandSender, message: String) {
        sender.sendMessage("§e[${plugin.name}]§r $message")
    }

    fun systemMessage(sender: CommandSender, messageType: MessageType, message: String) {
        sender.sendMessage("${messageType.prefix} " + message)
    }

    fun playerOnlyCmd(sender: CommandSender) {
        sender.sendMessage(MessageType.DEFAULT.prefix + " 플레이어만 입력 가능한 명령어입니다.")
    }
}