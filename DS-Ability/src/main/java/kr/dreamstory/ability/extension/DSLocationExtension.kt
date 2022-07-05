package kr.dreamstory.ability.extension

import kr.dreamstory.ability.ability.main
import org.bukkit.Location

fun locationToStringData(loc: Location?): String {
    return if (loc == null) "NULL" else "${loc.world.name} : ${loc.x} : ${loc.y} : ${loc.z} : ${loc.yaw} : ${loc.pitch}"
}

private fun parseLocation(str: String): Location {
    val args = str.split(" : ").toTypedArray()
    return Location(
        main.server.getWorld(args[0]),
        args[1].toDouble(),
        args[2].toDouble(),
        args[3].toDouble(),
        args[4].toFloat(),
        args[5].toFloat()
    )
}

fun String.parseLocation(dest: Boolean = false): Location? {
    return if(dest) { return if(this == "none") null else parseLocation(this) }
    else parseLocation(this)
}