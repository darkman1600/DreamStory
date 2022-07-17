package kr.dreamstory.shop

import kr.dreamstory.library.economy.PaymentType
import kr.dreamstory.library.extension.payment
import kr.dreamstory.library.message.MessageManager
import kr.dreamstory.library.message.MessageType
import kr.dreamstory.shop.prize.Price.Companion.format
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
            1 -> StringUtil.copyPartialMatches(args[0], listOf("open","edit","list","reload","money","cash"), list)
            2 -> when(args[0]) {
                "open","edit" -> StringUtil.copyPartialMatches(args[1], ShopManager.shopMap.keys, list)
                "money","cash" -> StringUtil.copyPartialMatches(args[1], listOf("add"), list)
                else -> list
            }
            else -> list
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) MessageManager.pluginMessage(main, sender,
            "§e/${command.name} open <상점이름> - §f상점을 오픈합니다." +
                    "\n§e/${command.name} edit <상점이름> - §f상점을 편집합니다." +
                    "\n§e/${command.name} list - §f등록 되어있는 상점 목록을 확인합니다." +
                    "\n§e/${command.name} reload - §f상점 목록을 새로고침합니다.")
        else when(args[0]) {
            "open" -> {
                if (args.size != 2) return false
                val shopData = ShopManager.shopMap[args[1]]
                if (shopData == null) MessageManager.systemMessage(sender,MessageType.WARNING,"해당 상점을 찾을 수 없습니다 : ${args[1]}")
                else ShopManager.openGui(sender,shopData)
            }
            "list" -> MessageManager.systemMessage(sender,MessageType.DEFAULT,"§6상점 목록 :\n${ShopManager.shopMap.keys.joinToString { "[$it]" }}")
            "reload" -> ShopManager.reload()
            "money","cash" -> {
                val type = PaymentType.fromString(args[0].uppercase())!!
                val payment = sender.payment(type)?: run { MessageManager.systemMessage(sender,MessageType.WARNING,"Payment를 가져오지 못했습니다."); return false }
                if (args.size == 1) MessageManager.systemMessage(sender,MessageType.DEFAULT,"§6잔액 §7: §f${type.format(payment.balance)}")
                else when(args[1]) {
                    "add" -> {
                        if (args.size != 3) return false
                        else {
                            val number = args[2].toLongOrNull()?: return false
                            payment.add(number)
                            MessageManager.systemMessage(sender,MessageType.DEFAULT,"§6잔액 §7: §f${type.format(payment.balance)}")
                        }
                    }
                }
            }
        }
        return true
    }
}