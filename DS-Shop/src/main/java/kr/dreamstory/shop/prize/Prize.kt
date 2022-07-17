package kr.dreamstory.shop.prize

import kr.dreamstory.library.extension.addLores
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.item.minecraft.api.ItemStackBuilder
import kr.dreamstory.shop.prize.Price.Companion.getColorFormat
import kr.dreamstory.shop.prize.Price.Companion.getStringFormat
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Prize(
    private val key: String,
    private val itemKey: String,
    val price: Price
) {

    val item: ItemStack?
        get() = DSItemManager.getDSItem(itemKey)?.itemStack?.clone()

    val icon: ItemStack
        get() {
            val icon = item
            val lore = arrayListOf<String>()
            price.run {
                if (toBuy != null) lore.add("§7[좌클릭]( 1개 구매 ) :" + type.getColorFormat(" - " + type.getStringFormat(toBuy)))
                if (toSell != null) lore.add("§7[우클릭]( 1개 판매 ) :" + type.getColorFormat(" + " + type.getStringFormat(toSell)))
                if (toBuy != null && stackable) lore.add("§7[쉬프트 + 좌클릭]( 64개 구매 ) :" + type.getColorFormat(" - " + type.getStringFormat(toBuy * 64)))
                if (toSell != null && stackable) lore.add("§7[쉬프트 + 우클릭]( 64개 판매 ) :" + type.getColorFormat(" + " + type.getStringFormat(toSell * 64)))
                return icon?.addLores(lore) ?: ItemStackBuilder.build(Material.RABBIT_HIDE,"§c아이템 정보 없음", listOf(key),32)
            }
        }

}