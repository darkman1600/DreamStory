package com.dreamstory.ability.util

import com.dreamstory.ability.ability.channel.ChannelType
import org.bukkit.Location

class DSLocation(type: String, loc: Location?, private val sLoc: String) {

    var serverType: ChannelType
    var islandServerName: String? = null
        private set
    val location: Location?
    val isChannel get() = serverType == ChannelType.SERVER

    init {
        try {
            serverType = ChannelType.valueOf(type)
        } catch (e: Exception) {
            serverType = ChannelType.ISLAND
            islandServerName = type
        }
        location = loc
    }

    override fun toString(): String = sLoc
}