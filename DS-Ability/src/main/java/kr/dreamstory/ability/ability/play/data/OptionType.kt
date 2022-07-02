package kr.dreamstory.ability.ability.play.data

enum class OptionType(val index: Int, val label: String) {

    ALL_ACCEPT(0,"§e전체 허용"),
    ONLY_FRIEND(1,"§e친구만"),
    ONLY_ISLAND(2,"§a섬원만"),
    ISLAND_FRIEND(3,"§e친구§f + §a섬원"),
    ALL_DENY(4,"§c전체 비허용");

    val next: OptionType
        get() {
            var index = this.index + 1
            if(index >= values().size) index = 0
            return indexOf(index)
        }

    companion object {
        fun indexOf(index: Int): OptionType =
            when(index) {
                0 -> ALL_ACCEPT
                1 -> ONLY_FRIEND
                2 -> ONLY_ISLAND
                3 -> ISLAND_FRIEND
                4 -> ALL_DENY
                else -> ALL_ACCEPT
            }
    }

}