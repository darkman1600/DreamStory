package kr.dreamstory.ability.commands

import kr.dreamstory.ability.commands.island.IslandCommand
import com.dreamstory.ability.manager.ChannelManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class IslandCommand:CommandExecutor,TabCompleter {

    override fun onCommand(p: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if(p !is Player) return false
        if(ChannelManager.isMainServer) {
            p.sendMessage("스폰.")
            IslandCommand.onCommand(p, args)
        }
        else  {
            p.sendMessage("섬.")
            IslandCommand.onCommand(p, args)
        }
        return true
    }

    override fun onTabComplete(p: CommandSender, p1: Command, p2: String, args: Array<String>): ArrayList<String> {
        if(p !is Player) return arrayListOf()
        if(ChannelManager.isMainServer) IslandCommand.onTabComplete(p, args)
        else  IslandCommand.onTabComplete(p, args)
        return arrayListOf()
    }


}