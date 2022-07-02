package com.dreamstory.ability.util

import com.google.gson.Gson
import net.minecraft.nbt.NBTTagCompound
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

inline fun <reified T> ItemStack.addNBTTagCompound(data: T): ItemStack {
    return CraftItemStack.asBukkitCopy(
        CraftItemStack.asNMSCopy(this).apply {
            this.b(NBTTagCompound().apply { a(T::class.simpleName, Gson().toJson(data)) })
        }
    )
}

inline fun <reified T> ItemStack.getNBTTagCompound(data: Class<T>): T? {
    val jsonData = CraftItemStack.asNMSCopy(this).t()?.l(T::class.simpleName) ?: return null
    return Gson().fromJson(jsonData, data)
}