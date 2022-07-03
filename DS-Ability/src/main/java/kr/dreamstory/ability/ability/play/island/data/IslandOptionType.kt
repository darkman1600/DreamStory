package kr.dreamstory.ability.ability.play.island.data

enum class IslandOptionType(val index: Int,val label: String) {

    OWNER(0,"§6섬장"),
    SUB_OWNER(1,"§6섬장§f, §e부섬장"),
    ALL(2,"§f모두");

    val next get() = indexOf(index+1)

    companion object {
        fun indexOf(index: Int): IslandOptionType {
            return when(index) {
                0-> OWNER
                1-> SUB_OWNER
                2-> ALL
                else-> OWNER
            }
        }
    }

}