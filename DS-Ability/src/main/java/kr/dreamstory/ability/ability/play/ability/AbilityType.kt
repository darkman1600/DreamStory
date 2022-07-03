package kr.dreamstory.ability.ability.play.ability

import org.bukkit.Material

enum class AbilityType(
    val index: Int,
    val label: String,
    val tool: Material
) {
    FARM(0,"채집", Material.NETHERITE_HOE),
    MINE(1,"채광", Material.NETHERITE_PICKAXE),
    HUNT(2,"사냥", Material.NETHERITE_SWORD),
    FISH(3,"낚시", Material.FISHING_ROD),
    NONE(4,"없음", Material.AIR);

    companion object {
        fun labelOf(label: String): AbilityType {
            return when(label) {
                "채집"-> FARM
                "채광"-> MINE
                "사냥"-> HUNT
                "낚시"-> FISH
                else-> NONE
            }
        }

        fun indexOf(index: Int): AbilityType {
            return when(index) {
                0-> FARM
                1-> MINE
                2-> HUNT
                3-> FISH
                else-> NONE
            }
        }

        fun getLabels(): List<String> = listOf("채집","채광","사냥","낚시")
    }
}