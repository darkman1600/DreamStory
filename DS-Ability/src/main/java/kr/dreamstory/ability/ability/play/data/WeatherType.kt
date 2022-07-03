package kr.dreamstory.ability.ability.play.data

enum class WeatherType(val index: Int, val label: String, val type: org.bukkit.WeatherType) {

    SUN(0,"§a맑음", org.bukkit.WeatherType.CLEAR),RAIN(1,"§b비", org.bukkit.WeatherType.DOWNFALL);

    val next get() = indexOf(if(index == 1) 0 else 1)

    companion object {
        fun indexOf(index: Int): WeatherType = if(index == 1) RAIN else SUN
    }

}