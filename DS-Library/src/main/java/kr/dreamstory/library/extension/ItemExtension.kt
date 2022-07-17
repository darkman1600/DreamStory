package kr.dreamstory.library.extension

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayOutputStream

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

@Throws(IllegalStateException::class)
fun ItemStack.toBase64(): String {
    return try {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeInt(1)
        dataOutput.writeObject(this)
        dataOutput.close()
        Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("아이템 저장에 실패하였습니다.", e)
    }
}

