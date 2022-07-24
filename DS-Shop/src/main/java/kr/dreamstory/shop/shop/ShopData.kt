package kr.dreamstory.shop.shop

import kr.dreamstory.library.extension.payment
import kr.dreamstory.library.utils.message.MessageManager
import kr.dreamstory.library.utils.message.MessageType
import kr.dreamstory.shop.category.Category
import kr.dreamstory.shop.prize.Price.Companion.format
import kr.dreamstory.shop.prize.Prize
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.math.ceil

class ShopData(
    private val key: String,
    val size: Int,
    val title: String,
    val voidSlots: List<Int>,
    val prizeSlots: List<Int>,
    val categorySlots: List<Int>,
    val categories: List<Category>
) {

    fun buy(player: Player, prize: Prize, number: Int): Boolean {
        val type = prize.price.type
        val payment = player.payment(type)?: run { MessageManager.systemMessage(player, MessageType.WARNING,"Payment를 가져오지 못했습니다."); return false }
        val price = prize.price.toBuy!! * number
        val item = prize.item?: run { MessageManager.systemMessage(player, MessageType.WARNING,"아이템 정보를 찾을 수 없습니다."); return false }
        if (payment.balance < price) { MessageManager.systemMessage(player, MessageType.WARNING,"잔액이 부족합니다. §6잔액 §7: §f${type.format(payment.balance)}"); return false }
        if (player.inventory.getEmptySlotSize() < ceil((number.toDouble() / item.maxStackSize))) { MessageManager.systemMessage(player,
            MessageType.WARNING,"인벤토리에 공간이 부족합니다."); return false }
        repeat(ceil(number.toDouble() / item.maxStackSize).toInt()) { player.inventory.addItem(item.asQuantity(number.coerceAtMost(item.maxStackSize))); number - item.maxStackSize }
        payment.unSafeRemove(price)
        MessageManager.systemMessage(player, MessageType.SUCCESS,"§b구매 성공 §7- §a${item.displayName} §7x $number\n§6잔액 §7: §f${type.format(payment.balance)}")
        player.playSound(player.location, Sound.BLOCK_AMETHYST_BLOCK_PLACE,1.5f,1f)
        return true
    }

    fun sell(player: Player, prize: Prize, number: Int): Boolean {
        val type = prize.price.type
        val payment = player.payment(type)?: run { MessageManager.systemMessage(player, MessageType.WARNING,"Payment를 가져오지 못했습니다."); return false }
        val price = prize.price.toSell!! * number
        val item = prize.item?: run { MessageManager.systemMessage(player, MessageType.WARNING,"아이템 정보를 찾을 수 없습니다."); return false }
        val leftItems = player.inventory.removeItem(item.asQuantity(number))
        if (leftItems.isNotEmpty()) {
            player.inventory.addItem(item.asQuantity(number - leftItems[0]!!.amount))
            MessageManager.systemMessage(player, MessageType.WARNING,"판매할 아이템이 부족합니다.")
            return false
        }
        payment.add(price)
        MessageManager.systemMessage(player, MessageType.SUCCESS,"§b판매 성공 §7- §a${item.displayName} §7x $number\n§6잔액 §7: §f${type.format(payment.balance)}")
        player.playSound(player.location, Sound.BLOCK_AMETHYST_BLOCK_PLACE,1.5f,1f)
        return true
    }

    private fun Inventory.getEmptySlotSize() = storageContents?.count { it == null || it.type == Material.AIR } ?: 36

}