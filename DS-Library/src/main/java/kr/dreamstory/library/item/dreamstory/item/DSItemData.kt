package com.dreamstory.library.item.dsitem.objs

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

abstract class DSItemData {

    abstract fun isLoaded(): Boolean
    abstract fun getFile(): File
    abstract fun createFile()
    abstract fun getConfig(): YamlConfiguration
    abstract fun getDSItems(): List<DSItemStack>

}