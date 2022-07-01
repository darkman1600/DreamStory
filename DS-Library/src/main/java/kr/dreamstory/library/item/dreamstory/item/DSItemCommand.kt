package kr.dreamstory.library.item.dreamstory.item

import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.item.dreamstory.item.guis.DSItemMainGUI
import kr.dreamstory.library.main
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.library.message.MessageType
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class DSItemCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, p1: Command, command: String, args: Array<out String>): List<String> {
        val list = ArrayList<String>()
        when(args.size) {
            1 -> return StringUtil.copyPartialMatches(args[0],listOf("reload","give","list"),list)
            2 -> {
                when(args[0]) {
                    "give" -> return StringUtil.copyPartialMatches(args[1],Bukkit.getOnlinePlayers().map { it.name },list)
                }
            }
            3 -> {
                when(args[0]) {
                    "give" -> return StringUtil.copyPartialMatches(args[2], DSItemManager.getDSItemKeyList(),list)
                }
            }
            4 -> {
                when(args[0]) {
                    "give" -> {
                        return listOf("<갯수>")
                    }
                }
            }
        }
        return list
    }

    override fun onCommand(sender: CommandSender, p1: Command, command: String, args: Array<out String>): Boolean {
        if(args.isEmpty()) {
            MessageManager.pluginMessage(main, sender,
                "§e/$command reload - §f파일에 저장 된 아이템을 다시 불러옵니다." +
                        "\n§e/$command give <닉네임> <아이템키> <갯수> - §f플레이어에게 아이템을 지급합니다." +
                        "\n§e/$command list - §f등록 되어있는 아이템의 목록을 확인합니다.")
        } else {
            when(args[0]) {
                "reload" -> {
                    if(args.size != 1) { MessageManager.pluginMessage(main,sender,"잘못 된 명령어입니다. \n§e$command reload"); return false }
                    DSItemManager.load(sender)
                }
                "give" -> {
                    if(args.size != 4) { MessageManager.systemMessage(sender, MessageType.DEFAULT, "잘못 된 명령어입니다.\n§e/$command give <닉네임> <아이템키> <갯수>"); return false }
                    val player = Bukkit.getPlayer(args[1])
                    if(player == null) { MessageManager.systemMessage(sender, MessageType.DEFAULT,"플레이어를 찾을 수 없습니다."); return false }
                    val item = DSItemManager.getDSItem(args[2])
                    if(item == null) { MessageManager.systemMessage(sender, MessageType.DEFAULT,"아이템을 찾을 수 없습니다."); return false }
                    val amount = args[3].toIntOrNull()
                    if(amount == null) { MessageManager.systemMessage(sender, MessageType.DEFAULT,"갯수는 정수 형태만 입력할 수 있습니다."); return false }
                    player.inventory.addItem(item.itemStack.asQuantity(amount))
                }
                "list" -> {
                    if(args.size != 1) { MessageManager.systemMessage(sender, MessageType.DEFAULT,"잘못 된 명령어입니다.\n§e/$command list"); return false }
                    if(sender is Player) DSItemMainGUI().open(sender)
                    else MessageManager.playerOnlyCmd(sender)
                }
            }
        }
        return true
    }
}