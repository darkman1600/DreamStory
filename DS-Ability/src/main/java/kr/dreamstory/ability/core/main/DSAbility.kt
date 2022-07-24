package kr.dreamstory.ability.core.main

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.subCore
import kr.dreamstory.ability.commands.channel.AbilityBlockCommand
import kr.dreamstory.ability.commands.channel.DropItemCommand
import kr.dreamstory.ability.core.sub.ChannelCore
import kr.dreamstory.ability.listener.DSDefaultListener
import kr.dreamstory.ability.listener.PlayerDataListener
import com.dreamstory.ability.listener.SuperJumpListener
import com.dreamstory.ability.listener.ability.*
import kr.dreamstory.ability.manager.ActionBarManager
import kr.dreamstory.ability.commands.*
import kr.dreamstory.ability.listener.ability.AbilityBlockListener
import kr.dreamstory.ability.listener.ability.AbilityBlockRegisterListener
import kr.dreamstory.ability.listener.ability.RegionListener
import kr.dreamstory.ability.listener.ability.SkillListener
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DSAbility : JavaPlugin(), Listener {

    var remover = false

    override fun onEnable() {
        main = this
        subCore = ChannelCore(this)
        subCore.onEnable()
        ActionBarManager.run(100)

        cmd(mapOf(
            "특성툴" to AbilityBlockCommand(),
            "전리품" to DropItemCommand(),
            "특성관리" to AbilityCommand(),
            "지역" to RegionCommand(),
            "스킬" to SkillCommand(),
            "스폰" to SpawnCommand()
        ))

        registerListeners(
            AbilityBlockListener(),
            AbilityBlockRegisterListener(),
            CommandListener(),
            RegionListener(),
            SkillListener(),
            PlayerDataListener(),
            DSDefaultListener(),
            SuperJumpListener()
        )

    }

    override fun onDisable() {
        if(remover) return
        subCore.onDisable()
    }

    private fun registerListeners(vararg listener: Listener) {
        val pluginManager = server.pluginManager
        listener.forEach {
            pluginManager.registerEvents(it, this)
        }
    }

    private fun cmd(commandMap: Map<String, CommandExecutor>) {
        commandMap.forEach { (cmd, it) ->
            val command = getCommand(cmd)
            command?.setExecutor(it)
            if(it is TabCompleter) command?.tabCompleter = it
        }
    }

    private inline fun checkInit(check: ()->Boolean): Boolean = if(!check()) { server.shutdown(); remover = true; true } else false
}