package kr.dreamstory.ability.ability.play.ability

import org.bukkit.Material

enum class AbilityType(
    val index: Int,
    val label: String,
    val tool: Material
) {
    MINE(0,"채광", Material.NETHERITE_PICKAXE),
    FARM(1,"채집", Material.NETHERITE_HOE),
    FISH(2,"낚시", Material.FISHING_ROD),
    HUNT(3,"사냥", Material.NETHERITE_SWORD),
    NONE(4,"없음", Material.AIR);

    companion object {
        fun labelOf(label: String): AbilityType {
            return when(label) {
                "채광" -> MINE
                "채집" -> FARM
                "낚시" -> FISH
                "사냥" -> HUNT
                else-> NONE
            }
        }

        fun indexOf(index: Int): AbilityType {
            return when(index) {
                0 -> MINE
                1 -> FARM
                2 -> FISH
                3 -> HUNT
                else-> NONE
            }
        }

        fun getLabels(): List<String> = listOf("채집","채광","사냥","낚시")
    }
}