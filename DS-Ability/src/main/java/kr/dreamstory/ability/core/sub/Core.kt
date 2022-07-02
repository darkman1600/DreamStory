package com.dreamstory.ability.core.sub

import kr.dreamstory.ability.core.main.DSAbility
import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.messaging.PluginMessageListener

abstract class Core(private val core: DSAbility): PluginMessageListener {

    val server by lazy { core.server }
    val pluginManager by lazy { core.server.pluginManager }

    init {
        val messenger = server.messenger
        messenger.registerOutgoingPluginChannel(core, "BungeeCord")
        messenger.registerIncomingPluginChannel(core, "BungeeCord",this)
    }

    abstract fun onEnable()
    abstract fun onDisable()
    fun getCommand(cmd: String): PluginCommand = core.getCommand(cmd)!!
    fun registerEvents(vararg listeners: Listener) { listeners.forEach { pluginManager.registerEvents(it, core) } }
    fun cmd(cmd: String,executor: CommandExecutor) {
        val command = getCommand(cmd)
        command.setExecutor(executor)
        if(executor is TabCompleter) command.tabCompleter = executor
    }
    fun sendPluginMessage(p: Player, byteArray: ByteArray) { p.sendPluginMessage(core, "BungeeCord", byteArray) }

}