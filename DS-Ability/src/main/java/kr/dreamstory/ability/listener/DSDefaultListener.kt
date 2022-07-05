package kr.dreamstory.ability.listener

import kr.dreamstory.ability.ability.main
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

class DSDefaultListener: Listener {

    private val cancelDamages by lazy {
        arrayOf(
            DamageCause.FALL
        )
    }

    private val commandList by lazy {
        arrayOf(
            "채널",
            "cosjf",
            "channel",
            "섬",
            "우편함"
        )
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun fallingBlockCancel(e: EntityChangeBlockEvent) { if(e.entityType == EntityType.FALLING_BLOCK) e.isCancelled = true }
    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerFallDamageCancel(e: EntityDamageEvent) {
        val entity = e.entity
        if(entity is Player && cancelDamages.contains(e.cause)) e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerDropItem(e: PlayerDropItemEvent) {
        val player = e.player
        if(e.player.world == main.server.worlds.first() && !player.isOp) e.isCancelled = true
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityPortalEvent(e: EntityPortalEvent) { if(e.entity !is Player) e.isCancelled = true }
    @EventHandler(priority = EventPriority.HIGHEST) fun handSwapEvent(e: PlayerSwapHandItemsEvent) { e.isCancelled = true }
    @EventHandler(priority = EventPriority.HIGHEST)
    fun equipLeftHandItem(e: InventoryClickEvent) {
        val inv = e.clickedInventory ?: return
        val type = inv.type
        if(type == InventoryType.PLAYER) {
            if(e.click == ClickType.SWAP_OFFHAND) e.isCancelled = true
            else if(e.slotType == InventoryType.SlotType.QUICKBAR) { if(e.slot == 40) e.isCancelled = true }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun equipLeftHandItem(e: InventoryDragEvent) {
        val inv = e.inventory
        val type = inv.type
        if(type == InventoryType.CRAFTING || type == InventoryType.CREATIVE)
            if(e.rawSlots.contains(45)) e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun settingCommands(e: PlayerCommandSendEvent) {
        val p = e.player
        if(p.isOp) return
        e.commands.clear()
        e.commands.addAll(commandList)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityFishingHookEvent(e: ProjectileHitEvent) { if(e.entity is FishHook) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onToolChange(e: PlayerItemHeldEvent) { if(e.previousSlot.equals(ItemStack(Material.FISHING_ROD)) || e.player.hasCooldown(Material.BAT_SPAWN_EGG)) e.player.setCooldown(Material.BAT_SPAWN_EGG, 0) }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerCommandEvent(e: PlayerCommandPreprocessEvent) {
        if (e.isCancelled) return
        val p = e.player
        val args = e.message.split(" ").toTypedArray()
        val cmd = if (args[0].contains("/")) args[0].substring(1) else args[0]
        if (cmd != "is") {
            if (p.isOp) return
            if (!commandList.contains(cmd)) {
                e.isCancelled = true
                p.sendMessage("§c올바른 명령어가 아닙니다.")
            }
        }
    }
}