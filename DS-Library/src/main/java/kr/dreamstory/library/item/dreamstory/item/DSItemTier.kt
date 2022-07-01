package com.dreamstory.library.item.dsitem.objs

enum class DSItemTier(
    val display: String,
    val color: String,
    val upgradePercent: Double
) {
    DEFAULT("기본","§f",1.0),
    RARE("고급","§a",0.85),
    UNIQUE("희귀","§e",0.65),
    LEGENDARY("전설","§6",0.3);

    companion object {
        fun getDSItemTier(tier: String) = tierMap[tier]
        private val tierMap = HashMap<String, DSItemTier>()
        init {
            values().forEach {
                tierMap[it.name] = it
            }
        }
    }
}