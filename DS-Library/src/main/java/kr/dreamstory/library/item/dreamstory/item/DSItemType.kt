package com.dreamstory.library.item.dsitem.objs

import org.bukkit.Material

enum class DSItemType(val display: String,val material:Material,val slot: Int) {

    DEFAULT("기본", Material.STONE,10),
    TOOL("도구", Material.WOODEN_PICKAXE,11),
    ARMOR("방어구", Material.NETHERITE_CHESTPLATE,12),
    FOOD("음식", Material.PUMPKIN_PIE,13),
    UPGRADE_STONE("강화 재료", Material.GLOWSTONE_DUST,14);

    companion object {
        fun getTypeFromSlot(slot: Int): DSItemType? = values().firstOrNull() { it.slot == slot }
        fun getDSItemType(type: String) = typeMap[type]
        private val typeMap = HashMap<String, DSItemType>()
        init {
            values().forEach {
                typeMap[it.name] = it
            }
        }
    }

}