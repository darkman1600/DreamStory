package kr.dreamstory.library.item.dreamstory.api

import org.bukkit.Material

object Icons {
    val plusIcon by lazy {
        ItemStackBuilder.build(Material.RABBIT_HIDE, 21)
    }
    val crossIcon by lazy {
        ItemStackBuilder.build(Material.RABBIT_HIDE, 32)
    }
    val arrow_right by lazy {
        ItemStackBuilder.build(Material.RABBIT_HIDE, 2)
    }
}