package kr.dreamstory.ability.ability.play.region

enum class RegionType(val index: Int, val label: String) {

    NONE(0,"§7알 수 없음"),
    FIELD(1,"§a필드"),
    VILLAGE(2,"§6마을"),
    DUNGEON(3,"§4던전"),;

    val next: RegionType
        get() {
            var index = this.index + 1
            if(index > 6) index = 0
            return indexOf(index)
        }

    companion object {
        fun indexOf(index: Int): RegionType
            = try {
                values().filter { it.index == index }[0]
            } catch (e: Exception) {
                NONE
            }

    }

}