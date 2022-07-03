package kr.dreamstory.shop


import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.item.minecraft.api.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var main: JavaPlugin
internal fun getNullItem() = DSItemManager.getNewDSItem("null", ItemStackBuilder.build(Material.RABBIT_HIDE,32),false).itemStack