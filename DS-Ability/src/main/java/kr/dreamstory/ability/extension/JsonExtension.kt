package com.dreamstory.ability.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T : Any> T.toJson(gson: Gson = Gson()): String {
    return gson.toJson(this)
}

inline fun <reified T> String.fromJson(gson: Gson = Gson()): T {
    val type = object : TypeToken<T>() {}.type
    return gson.fromJson(this, type)
}