package com.dreamstory.ability.extension

import com.dreamstory.ability.ability.channel.ChannelType
import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.manager.ChannelManager
import com.dreamstory.ability.util.DSLocation
import org.bukkit.Location

fun locationToDSLocationString(loc: Location?): String {
    val sLoc = if (loc == null) "NULL" else "${loc.world.name} : ${loc.x} : ${loc.y} : ${loc.z} : ${loc.yaw} : ${loc.pitch}"
    return if (ChannelType.SERVER == ChannelManager.type) "${ChannelType.SERVER.name} : $sLoc" else "${ChannelManager.name} : $sLoc"
}

private fun parseDSLocation(loc: String): DSLocation {
    return try {
        val args = loc.split(" : ").toTypedArray()
        return if (args[1].equals("NULL", true)) DSLocation(args[0], null, loc) else try {
            DSLocation(
                args[0],
                Location(
                    main.server.getWorld(args[1]),
                    args[2].toDouble(),
                    args[3].toDouble(),
                    args[4].toDouble(),
                    args[5].toFloat(),
                    args[6].toFloat()
                ),
                loc
            )
        } catch (e: Exception) {
            DSLocation(args[0], null, loc)
        }
    } catch (e: Exception) {
        DSLocation(if(ChannelManager.isMainServer) ChannelManager.type.toString() else ChannelManager.name, null, loc)
    }
}

fun String.parseDSLocation(dest: Boolean = false): DSLocation? {
    return if(dest) { return if(this == "none") null else parseDSLocation(this) }
    else parseDSLocation(this)
}