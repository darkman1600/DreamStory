package com.dreamstory.ability.extension

import kr.dreamstory.ability.ability.main
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

fun ItemStack.getKey() = getStringNbt("key")

fun ItemStack.getIntNbt(key: String) = itemMeta.persistentDataContainer[NamespacedKey(main,key), PersistentDataType.INTEGER]
fun ItemStack.getStringNbt(key: String) = itemMeta.persistentDataContainer[NamespacedKey(main,key), PersistentDataType.STRING]
fun ItemStack.getDoubleNbt(key: String) = itemMeta.persistentDataContainer[NamespacedKey(main,key), PersistentDataType.DOUBLE]
fun ItemStack.setIntNbt(key: String, int: Int) {
    val meta = itemMeta
    meta.persistentDataContainer.set(NamespacedKey(main,key), PersistentDataType.INTEGER, int)
    itemMeta = meta
}
fun ItemStack.setStringNbt(key: String, value: String) {
    val meta = itemMeta
    meta.persistentDataContainer.set(NamespacedKey(main,key), PersistentDataType.STRING, value)
    itemMeta = meta
}
fun ItemStack.setDoubleNbt(key: String, value: Double) {
    val meta = itemMeta
    meta.persistentDataContainer.set(NamespacedKey(main,key), PersistentDataType.DOUBLE, value)
    itemMeta = meta
}

fun ItemMeta.getIntNbt(key: String): Int? { return persistentDataContainer[NamespacedKey(main,key), PersistentDataType.INTEGER] }
fun ItemMeta.getDoubleNbt(key: String): Double? { return persistentDataContainer[NamespacedKey(main,key), PersistentDataType.DOUBLE] }
fun ItemMeta.getStringNbt(key: String): String? { return persistentDataContainer[NamespacedKey(main,key), PersistentDataType.STRING] }

fun ItemMeta.setIntNbt(key: String, int: Int): ItemMeta {
    return apply { persistentDataContainer.set(NamespacedKey(main,key), PersistentDataType.INTEGER, int) }
}
fun ItemMeta.setDoubleNbt(key: String, double: Double): ItemMeta {
    return apply { persistentDataContainer.set(NamespacedKey(main,key), PersistentDataType.DOUBLE, double) }
}
fun ItemMeta.setStringNbt(key: String, value: String): ItemMeta {
    return apply { persistentDataContainer.set(NamespacedKey(main,key), PersistentDataType.STRING, value) }
}
