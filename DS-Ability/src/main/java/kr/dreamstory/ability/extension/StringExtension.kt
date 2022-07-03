package com.dreamstory.ability.extension

import java.util.regex.Pattern

fun String.checkWords(): Boolean = Pattern.matches("^[0-9a-zA-Z가-힣]*\$", this)

fun Array<String>.cutLabel(cutIndex: Int): String {
    var result: String = this[cutIndex]
    for (i in cutIndex+1 until size) {
        result += " " + this[i]
    }
    return result.replace("&", "§")
}
