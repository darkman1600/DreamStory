package kr.dreamstory.ability.objs

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object AbilityBlockStick {

    private var stick: ItemStack? = null
    private const val displayName = "§e특성 지역 툴"

    fun getAbilityBlockStick(p: Player, type: String): Boolean {
        val lore = ArrayList<String>()
        when(type) {
            "채집" -> lore.add("§a채집")
            "채굴" -> lore.add("§7채굴")
            "사냥" -> lore.add("§c사냥")
            "낚시" -> lore.add("§9낚시")
            else -> return false
        }
        update(lore)
        p.inventory.addItem(stick!!.clone())
        return true
    }

    fun isStick(item: ItemStack?): Boolean { return if (item == null || item.type != Material.BLAZE_ROD || !item.hasItemMeta()) false else item.itemMeta.displayName == displayName }

    fun getDisplayName(): String { return displayName }

    private fun update(lore: List<String>) {
        stick = ItemStack(Material.BLAZE_ROD)
        val meta = stick!!.itemMeta
        meta.setDisplayName(displayName)
        meta.lore = lore
        stick!!.itemMeta = meta
    }
}