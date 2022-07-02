package kr.dreamstory.ability.commands.channel

import com.dreamstory.ability.extension.toDropGrade
import com.dreamstory.ability.interfaces.ChannelCommandExecutor
import com.dreamstory.ability.manager.DropItemManager
import com.dreamstory.ability.util.integerFormat
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DropItemCommand: ChannelCommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(sender !is Player) return false
        if(!sender.isOp) return false
        if(args.isEmpty()) {
            sender.sendMessage("§e/전리품 등록 < 가격 > : §f손에 든 아이템을 전리품으로 등록합니다.")
            sender.sendMessage("§e/전리품 가격 : §f손에 든 아이템의 가격을 확인합니다.")
            sender.sendMessage("§e/전리품 등급 < 등급 > : §f손에 든 아이템의 등급을 설정합니다.")
            return true
        }

        val item = sender.inventory.itemInMainHand
        if(item.type == Material.AIR) {
            sender.sendMessage("§c손에 든 아이템이 없습니다.")
            return true
        }

        when(args[0]) {
            "등록"-> {
                try {
                    val cost = Integer.parseInt(args[1])
                    DropItemManager.registerDropItem(item, cost)
                    sender.sendMessage("§7등록 되었습니다.")
                } catch (e: Exception) {
                    sender.sendMessage("§c가격을 제대로 입력하지 않았습니다.")
                }
            }
            "가격" -> {
                val cost = DropItemManager.getCost(item)
                sender.sendMessage("§7해당 아이템의 가격은 §e${cost.integerFormat()} §lS §7입니다.")
            }
            "등급"-> {
                try {
                    val grade = args[1].toDropGrade()
                    if(grade == null) {
                        sender.sendMessage("§c등급은 §f하급, 일반, 고급, 최고급, 전설 §c이 있습니다.")
                    }
                    else {
                        val temp = DropItemManager.setGrade(item, grade)
                        sender.inventory.addItem(temp)
                        sender.sendMessage("§7등급이 설정되어 인벤토리에 지급됩니다.")
                    }
                } catch (e: Exception) {
                    sender.sendMessage("§c등급은 §f하급, 일반, 고급, 최고급, 전설 §c이 있습니다.")
                }
            }
        }
        return false
    }
}