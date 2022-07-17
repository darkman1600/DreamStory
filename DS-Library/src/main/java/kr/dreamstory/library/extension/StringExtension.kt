package kr.dreamstory.library.extension

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.regex.Pattern

fun String.translateHexColorCodes(): String {
    val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
    var matcher = pattern.matcher(this)
    var message = this
    while (matcher.find()) {
        val hexCode: String = message.substring(matcher.start(), matcher.end())
        val replaceSharp = hexCode.replace('#', 'x')
        val ch = replaceSharp.toCharArray()
        val builder = StringBuilder("")
        for (c in ch) {
            builder.append("&$c")
        }
        message = message.replace(hexCode, builder.toString())
        matcher = pattern.matcher(message)
    }
    return ChatColor.translateAlternateColorCodes('&', message);
}
fun String.color(): String {
    return ChatColor.translateAlternateColorCodes('&',this)
}

@Throws(IOException::class)
fun String.toItemStackFromBase64(): ItemStack {
    return try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(this))
        val dataInput = BukkitObjectInputStream(inputStream)
        dataInput.readInt()
        val obj = dataInput.readObject()
        dataInput.close()
        val result: ItemStack = if(obj == null) ItemStack(Material.AIR)
        else obj as ItemStack
        result
    } catch (e: ClassNotFoundException) {
        ItemStack(Material.AIR)
    } catch (e: IOException) {
        ItemStack(Material.AIR)
    }
}