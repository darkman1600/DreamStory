package kr.dreamstory.library.item.minecraft

import org.bukkit.ChatColor
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