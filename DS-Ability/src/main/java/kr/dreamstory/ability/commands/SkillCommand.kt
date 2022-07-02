package kr.dreamstory.ability.commands

import kr.dreamstory.ability.ability.play.skills.gui.SkillGUI
import com.dreamstory.ability.extension.ability
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SkillCommand: CommandExecutor,TabCompleter {
    override fun onCommand(p: CommandSender, p1: Command, p2: String, array: Array<out String>): Boolean {
        if(p !is Player) return false
        p.ability?: return false
        SkillGUI(p)
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, s: String, args: Array<out String>): MutableList<String> {
        return ArrayList()
    }
}