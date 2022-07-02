package com.dreamstory.ability.util

import java.math.BigDecimal
import java.util.*
import kotlin.math.ln
import kotlin.random.Random

fun Double.percent(): Boolean {
    var temp = this
    if(temp > 100.0) temp = 100.0
    else if(temp < 0.0) temp = 0.0

    return when(temp) {
        100.0 -> true
        0.0 -> false
        else -> {
            val random = Random;
            val suc = BigDecimal(this)
            var fail = BigDecimal("100.0")
            fail = fail.subtract(suc)
            val entry = mapOf(true to suc.toDouble(),false to fail.toDouble()).entries.stream()
            entry
                .map { e-> AbstractMap.SimpleEntry(e.key, -ln(random.nextDouble()) /e.value) }
                .min(compareBy { it.value })
                .orElseThrow { IllegalArgumentException() }.key
        }
    }
}
fun Int.percent(): Boolean {
    val temp = toDouble()
    return temp.percent()
}
