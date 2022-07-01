package kr.dreamstory.library.item.minecraft.api

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object ItemStackBuilder {
    fun build(material: Material,customModelData: Int?) = ItemStack(material).apply { setCustomModelData(customModelData) }
    fun build(material: Material, name: String?) = ItemStack(material).apply { setDisplayName(name) }
    fun build(material: Material, lores: List<String>?) = ItemStack(material).apply { lore = lores }
    fun build(material: Material, name: String?, lores: List<String>?) = ItemStack(material).apply {
        lore = lores
        setDisplayName(name)
    }
    fun build(material: Material, name: String?, lores: List<String>?, customModelData: Int?, vararg hideFlags: ItemFlag): ItemStack {
        return ItemStack(material).apply {
            setDisplayName(name)
            lore = lores
            setCustomModelData(customModelData)
            addItemFlags(*hideFlags)
        }
    }

    fun buildHideFlags(material: Material, name: String?, lores: List<String>?, customModelData: Int?): ItemStack {
        return ItemStack(material).apply {
            setDisplayName(name)
            lore = lores
            setCustomModelData(customModelData)
            addItemFlags(*ItemFlag.values())
        }
    }
}