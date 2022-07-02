package com.dreamstory.ability.listener.island

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.listener.interfaces.ServerListener
import com.dreamstory.ability.manager.DSIslandManager
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import world.bentobox.bentobox.api.events.command.CommandEvent
import world.bentobox.bentobox.api.events.island.IslandCreatedEvent
import java.util.*

class IslandListener: ServerListener {

    companion object {
        private val commandPermission = HashSet<UUID>()

        fun Player.dispatchIslandCommand(command: String) {
            commandPermission.add(uniqueId)
            //performCommand(command)
            server.dispatchCommand(this, command)
            commandPermission.remove(uniqueId)
        }
    }

    @EventHandler
    fun onCommand(e: CommandEvent) {
        if(e.sender is Player) {
            val p = e.sender as Player
            if(e.label == "is") {
                if(!commandPermission.contains(p.uniqueId)) {
                    e.isCancelled = true
                    p.sendMessage("§c올바른 명령어가 아닙니다.")
                }
            } else {
                if(!p.isOp) {
                    e.isCancelled = true
                    p.sendMessage("§c올바른 명령어가 아닙니다.")
                }
            }
        }
    }

    @EventHandler
    fun createIsland(e: IslandCreatedEvent) {
        val owner = e.owner
        val island = e.island
        val p: Player = main.server.getPlayer(owner)!!
        DSIslandManager.registerIsland(p, island.getSpawnPoint(World.Environment.NORMAL)!!,island.uniqueId)
    }

}