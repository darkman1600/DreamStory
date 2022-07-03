package kr.dreamstory.shop

import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.library.message.MessageType
import kr.dreamstory.shop.shop.ShopManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class Command: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = arrayListOf<String>()
        return when(args.size) {
            1 -> StringUtil.copyPartialMatches(args[0], listOf("열기","편집","목록"), list)
            2 -> StringUtil.copyPartialMatches(args[0], ShopManager.shopMap.values.map { it.key }, list)
            else -> list
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) MessageManager.pluginMessage(main, sender,
            "§e/${command.name} 열기 <상점이름> - §f상점을 오픈합니다." +
                    "\n§e/${command.name} 편집 <상점이름> - §f상점을 편집합니다." +
                    "\n§e/${command.name} 목록 - §f등록 되어있는 상점 목록을 확인합니다.")
        else when(args[0]) {
            "열기" -> {
                if (args.size != 2) return false
                val shopData = ShopManager.shopMap[args[1]]
                if (shopData == null) MessageManager.systemMessage(sender,MessageType.WARNING,"해당 상점을 찾을 수 없습니다 : ${args[1]}")
                else ShopManager.openGui(sender,shopData)
            }
            "목록" -> MessageManager.systemMessage(sender,MessageType.DEFAULT,"§6상점 목록 :\n${ShopManager.shopMap.values.map { it.key }.toString().replace(",","\n§e")}")
        }
        return true
    }
}