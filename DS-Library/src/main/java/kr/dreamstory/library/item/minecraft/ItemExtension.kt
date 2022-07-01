package kr.dreamstory.library.item.minecraft

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

fun ItemStack.addLore(str: String): ItemStack {
    val newLore = this.lore ?: arrayListOf()
    newLore.add(str)
    this.lore = newLore
    return this
}

fun ItemStack.addLores(lore: List<String>): ItemStack {
    val newLore = this.lore ?: arrayListOf()
    newLore.addAll(lore)
    this.lore = newLore
    return this
}
fun ItemStack.setLores(lore: List<String>): ItemStack {
    this.lore = lore
    return this
}
fun ItemStack.setName(name: String?): ItemStack {
    setDisplayName(name)
    return this
}

fun ItemStack.setNameAndLore(name: String?,lores: List<String>?): ItemStack {
    setDisplayName(name)
    lore = lores
    return this
}

fun ItemStack.setGlow(): ItemStack {
    addEnchant(Enchantment.LURE,1,true)
    addItemFlags(ItemFlag.HIDE_ENCHANTS)
    return this
}

