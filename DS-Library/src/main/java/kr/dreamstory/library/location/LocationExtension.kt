package com.dreamstory.library.location

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

fun Location.getNearBlock(range: Int,type: Material): Block? {
    val r = range / 2
    val bx = blockX
    val by = blockY
    val bz = blockZ
    for(x in r * -1..r) {
        for(y in r * -1.. r) {
            for(z in r * -1..r) {
                val block = world.getBlockAt(bx + x,by + y,bz + z)
                if(block.type == type) return block
            }
        }
    }
    return null
}