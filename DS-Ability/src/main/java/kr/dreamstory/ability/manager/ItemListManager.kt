package com.dreamstory.ability.manager

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

object ItemListManager {

    @Throws(IllegalStateException::class)
    fun itemStackArrayToBase64(items1: Array<ItemStack?>? = null, items2: Array<ItemStack>? = null): String {
        var check = false
        val items: Array<ItemStack> = if(items2 != null) items2 else {
            check = true
            Array(items1!!.size) { ItemStack(Material.AIR) }
        }
        if(check)
            items1!!.forEachIndexed { i,it->items[i] = it?: return@forEachIndexed }
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeInt(items.size)
            items.forEach { dataOutput.writeObject(it) }
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }

    @Throws(IllegalStateException::class)
    fun itemStackArrayToBase64(item: ItemStack): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeInt(1)
            dataOutput.writeObject(item)
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }

    @Throws(IllegalStateException::class)
    fun toBase64(inventory: Inventory): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            val size = inventory.size
            if (size == 0) return "none"
            dataOutput.writeInt(size)
            for (i in 0 until size) dataOutput.writeObject(inventory.getItem(i))
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }

    @Throws(IOException::class)
    fun fromBase64(data: String): Inventory {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())
            for (i in 0 until inventory.size) {
                val obj = dataInput.readObject()
                if(obj != null) inventory.setItem(i, obj as ItemStack)
            }
            dataInput.close()
            inventory
        } catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }

    @Throws(IOException::class)
    fun itemStackArrayFromBase64(data: String): Array<ItemStack> {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val items:Array<ItemStack> = Array(dataInput.readInt()) {
                val obj = dataInput.readObject()
                if(obj == null) ItemStack(Material.AIR) else obj as ItemStack
            }
            dataInput.close()
            items
        } catch (e: IOException) {
            val items = Array(1) { ItemStack(Material.AIR) }
            items
        }
    }

    @Throws(IOException::class)
    fun singleItemStackArrayFromBase64(data: String?): ItemStack {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            dataInput.readInt()
            val obj = dataInput.readObject()
            dataInput.close()
            val result: ItemStack = if(obj == null) ItemStack(Material.AIR)
            else obj as ItemStack
            result
        } catch (e: ClassNotFoundException) {
            ItemStack(Material.AIR)
        } catch (e: IOException) {
            ItemStack(Material.AIR)
        }
    }
}