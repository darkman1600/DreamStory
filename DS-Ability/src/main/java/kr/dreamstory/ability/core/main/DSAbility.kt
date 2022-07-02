package kr.dreamstory.ability.core.main

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.subCore
import kr.dreamstory.ability.commands.channel.AbilityBlockCommand
import kr.dreamstory.ability.commands.channel.DropItemCommand
import com.dreamstory.ability.core.sub.ChannelCore
import com.dreamstory.ability.core.sub.IslandCore
import com.dreamstory.ability.interfaces.ChannelCommandExecutor
import com.dreamstory.ability.interfaces.IslandCommandExecutor
import com.dreamstory.ability.listener.DreamStoryDefaultListener
import com.dreamstory.ability.listener.GUIListener
import com.dreamstory.ability.listener.PlayerDataListener
import com.dreamstory.ability.listener.SuperJumpListener
import com.dreamstory.ability.listener.ability.*
import com.dreamstory.ability.listener.interfaces.ChannelListener
import com.dreamstory.ability.listener.island.IslandListener
import com.dreamstory.ability.manager.ChannelManager
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.manager.TaskManager
import kr.dreamstory.ability.commands.*
import kr.dreamstory.ability.java.pixelmaker.gui.PixelEvents
import kr.dreamstory.ability.java.pixelmaker.gui.PixelGUI
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DSAbility : JavaPlugin(), Listener {

    var remover = false

    override fun onEnable() {
        main = this
        saveResource("config.yml",false)
        if(checkInit { MysqlManager.setMysql() }) {
            println("쿼리 연동 실패")
            return
        }
        if(checkInit { ChannelManager.channelCheck(100) }) {
            println("채널체크 실패")
            return
        }
        when(ChannelManager.type) {
            ChannelType.ISLAND-> subCore = IslandCore(this)
            ChannelType.SERVER-> subCore = ChannelCore(this)
            else -> if(checkInit { false }) return
        }
        subCore.onEnable()
        TaskManager.run(100)

        cmd(mapOf(
            "특성툴" to AbilityBlockCommand(),
            "전리품" to DropItemCommand(),
            "특성관리" to AbilityCommand(),
            "섬" to IslandCommand(),
            "개인설정" to OptionCommand(),
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
            IslandListener(),
            PixelEvents(),
            GUIListener(),
            PlayerDataListener(),
            DreamStoryDefaultListener(),
            SuperJumpListener()
        )

    }

    override fun onDisable() {
        if(remover) return
        ChannelManager.removeChannel()
        subCore.onDisable()
        PixelGUI.serverDown()
    }

    private fun registerListeners(vararg listener: Listener) {
        val pluginManager = server.pluginManager
        listener.forEach {
            if(it is ChannelListener && !ChannelManager.isMainServer) return@forEach
            else if(it is IslandListener && ChannelManager.isMainServer) return@forEach
            pluginManager.registerEvents(it, this)
        }
    }

    private fun cmd(commandMap: Map<String, CommandExecutor>) {
        commandMap.forEach { (cmd, it) ->
            if(it is ChannelCommandExecutor && !ChannelManager.isMainServer) return@forEach
            if(it is IslandCommandExecutor && ChannelManager.isMainServer) return@forEach
            val command = getCommand(cmd)
            command?.setExecutor(it)
            if(it is TabCompleter) command?.tabCompleter = it
        }
    }

    private inline fun checkInit(check: ()->Boolean): Boolean = if(!check()) { server.shutdown(); remover = true; true } else false
}