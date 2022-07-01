package kr.dreamstory.library.packet

import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
import net.minecraft.server.network.PlayerConnection
import net.minecraft.world.entity.item.EntityItem
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftItem
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class ItemDisplay(
    val location: Location,
    val itemStack: ItemStack
) {
    private val craftWorld = (location.world as CraftWorld).handle
    private val entityItem = EntityItem(craftWorld,location.x,location.y,location.z,CraftItemStack.asNMSCopy(itemStack))
    private val bukkitItem = entityItem.bukkitEntity as CraftItem

    fun show(player: Player, options: ItemDisplayOptions?) {
        val craftPlayer = (player as CraftPlayer).handle.b

        bukkitItem.momentum = Vector(0,0,0)

        craftPlayer.a(PacketPlayOutSpawnEntity(entityItem))
        craftPlayer.a(PacketPlayOutEntityMetadata(bukkitItem.entityId,entityItem.ai(),true))
        craftPlayer.a(PacketPlayOutEntityVelocity(entityItem))

        if(options == null) return
        options.apply(craftPlayer)
    }

    fun hide(player: Player) {
        val connection = (player as CraftPlayer).handle.b
        connection.a(PacketPlayOutEntityDestroy(bukkitItem.entityId))
    }

    fun applyOptions(player: Player, options: ItemDisplayOptions) {
        val craftPlayer = (player as CraftPlayer).handle.b
        options.apply(craftPlayer)
    }

    inner class ItemDisplayOptions(private val customName: String?) {
        fun apply(craftPlayer: PlayerConnection) {
            if(customName != null) {
                bukkitItem.customName = customName
                bukkitItem.isCustomNameVisible = true
            }
            craftPlayer.a(PacketPlayOutEntityMetadata(bukkitItem.entityId,entityItem.ai(),true))
        }
    }
}