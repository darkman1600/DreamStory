package kr.dreamstory.ability.ability.play.block.obj

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.library.coroutine.schedule
import net.minecraft.core.BlockPosition
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
import net.minecraft.server.level.EntityPlayer
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_18_R2.CraftServer
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

interface BreakAbleBlock {

    companion object {
        private val scheduler by lazy { main.server.scheduler }
        private val craftServer by lazy { main.server as CraftServer }
        val breakPlayers = HashSet<UUID>()
        private val coolMap = HashMap<Block, BlockData>()
        fun startCoolTime(b: Block, blockData:BlockData, cool: Long) {
            val loc = b.location
            coolMap[b] = blockData
            scheduler.schedule(main) {
                waitFor(cool)
                for(entity in loc.getNearbyEntities(5.0,5.0,5.0)) {
                    if(breakPlayers.contains(entity.uniqueId)) {
                        startCoolTime(b,blockData,200)
                        return@schedule
                    }
                }
                coolMap.remove(b)
                b.blockData = blockData
            }
        }
        fun actionBlockBreakAnimation(b: Block, entityPlayer: EntityPlayer, damage: Int) {
            val world = (Bukkit.getWorlds()[0] as CraftWorld).handle
            val loc = b.location
            val packet = PacketPlayOutBlockBreakAnimation(entityPlayer.e, BlockPosition(loc.block.x, loc.block.y, loc.block.z), damage)
            craftServer.handle.a(null, loc.x, loc.y, loc.z, 120.0, world.aa(), packet)
        }

        fun resetCoolTimer() {
            try {
                coolMap.forEach { (block, blockData) -> block.blockData = blockData }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startBreak(player: Player, tool: ItemStack, block: Block)
    fun breakBlock(player: Player, block: Block, ability: Ability, isSkill: Boolean)
    //fun createHitBox(world: World, entityPlayer: EntityPlayer, block: Block): Int
    //fun removeHitBox(entityPlayer: EntityPlayer, hitBoxId: Int)
}