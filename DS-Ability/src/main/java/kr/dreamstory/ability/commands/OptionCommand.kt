package kr.dreamstory.ability.commands

import kr.dreamstory.ability.ability.play.ability.hud.HUDGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OptionCommand: CommandExecutor {
    override fun onCommand(p: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(p !is Player) return false
        HUDGUI(p)
        return false
    }
}