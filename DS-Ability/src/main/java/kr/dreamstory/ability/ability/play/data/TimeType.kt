package kr.dreamstory.ability.ability.play.data

enum class TimeType(val index: Int, val label:String, val tick: Long) {

    MORNING(0,"§e아침",1000),
    NOON(1,"§6점심",6000),
    EVENING(2,"§3저녁",13000),
    NIGHT(3,"§7밤",18000);

    val next: TimeType
        get() {
            var index = this.index + 1
            if(index >= values().size) index = 0
            return indexOf(index)
        }

    companion object {
        fun indexOf(index: Int): TimeType =
            when(index) {
                0 -> MORNING
                1 -> NOON
                2 -> EVENING
                3 -> NIGHT
                else -> MORNING
            }
    }

}