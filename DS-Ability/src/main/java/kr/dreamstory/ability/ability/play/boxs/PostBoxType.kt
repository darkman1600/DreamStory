package kr.dreamstory.ability.ability.play.boxs

enum class PostBoxType(val index: Int, val customModelNumber: Int, val label: String) {
    JOIN_ISLAND(0,1,"§a섬 초대"),ADD_FRIEND(1,2,"§e친구 요청"),
    COOP_ISLAND(2,3,"§a섬 알바초대");

    companion object {
        fun indexOf(index: Int): PostBoxType? {
            return when(index) {
                1-> JOIN_ISLAND
                2-> ADD_FRIEND
                3-> COOP_ISLAND
                else -> null
            }
        }
    }
}