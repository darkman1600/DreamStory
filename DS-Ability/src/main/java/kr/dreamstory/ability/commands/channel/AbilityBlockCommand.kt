package kr.dreamstory.ability.commands.channel

import com.dreamstory.ability.interfaces.ChannelCommandExecutor
import kr.dreamstory.ability.objs.AbilityBlockStick
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class AbilityBlockCommand: TabExecutor {
    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, args: Array<out String>): List<String> {
        val list = ArrayList<String>()
        if(args.size == 1) StringUtil.copyPartialMatches(args[0],listOf("채집","채굴","사냥","낚시"),list)
        return list
    }

    override fun onCommand(p: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(p !is Player) return false
        if(!p.isOp) return false
        when(args.size) {
            1 -> if(!AbilityBlockStick.getAbilityBlockStick(p,args[0])) p.sendMessage("§c채집,채굴,사냥,낚시 중 선택하여 입력하세요.")
            else -> p.sendMessage("/$p2 < abilityType >")
        }
        return true
    }
}