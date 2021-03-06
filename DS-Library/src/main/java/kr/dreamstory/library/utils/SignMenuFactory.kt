package kr.dreamstory.library.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.BlockPosition
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.TileEntitySign
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.function.BiPredicate

class SignMenuFactory(private val plugin: Plugin) {
    private val inputReceivers: MutableMap<Player, Menu>
    fun newMenu(text: List<String>): Menu {
        Objects.requireNonNull(text, "text")
        return Menu(text)
    }

    private fun listen() {
        ProtocolLibrary.getProtocolManager()
            .addPacketListener(object : PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
                override fun onPacketReceiving(event: PacketEvent) {
                    val player = event.player
                    val menu = inputReceivers.remove(player) ?: return
                    event.isCancelled = true
                    val success = menu.response!!.test(player, event.packet.stringArrays.read(0))
                    if (!success) {
                        main.schedule(SynchronizationContext.SYNC) {
                            waitFor(2)
                            menu.open(player)
                        }
                    }
                    player.sendBlockChange(menu.position!!.toLocation(player.world), Material.AIR.createBlockData())
                }
            })
    }

    inner class Menu internal constructor(private val text: List<String>) {
        var response: BiPredicate<Player, Array<String>>? = null
        private var reopenIfFail = false
        var position: BlockPosition? = null
            private set

        fun opensOnFail(): Boolean {
            return reopenIfFail
        }

        fun reopenIfFail(): Menu {
            reopenIfFail = true
            return this
        }

        fun response(response: BiPredicate<Player, Array<String>>?): Menu {
            this.response = response
            return this
        }

        fun open(player: Player) {
            val craftPlayer = (player as CraftPlayer).handle
            val location = player.location.apply { y = .0 }
            position = BlockPosition(location.blockX,0,location.blockZ)
            val nmsPosition = net.minecraft.core.BlockPosition(location.blockX,0,location.blockZ)
            player.sendBlockChange(location.apply { y = .0 },Material.OAK_SIGN.createBlockData())
            val lores = text.map { CraftChatMessage.fromString(it)[0] }
            val sign = TileEntitySign(nmsPosition,Blocks.b.n())
            sign.apply {
                for(i in 0..3) a(i,lores[i])
            }
            craftPlayer.b.a(sign.c())
            val signPacket = PacketPlayOutOpenSignEditor(nmsPosition)
            craftPlayer.b.a(signPacket)

            inputReceivers[player] = this
        }

        private fun color(input: String): String {
            return ChatColor.translateAlternateColorCodes('&', input)
        }
    }

    init {
        inputReceivers = java.util.HashMap()
        listen()
    }
}