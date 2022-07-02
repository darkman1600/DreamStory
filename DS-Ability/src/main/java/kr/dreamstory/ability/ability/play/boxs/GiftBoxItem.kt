package kr.dreamstory.ability.ability.play.boxs

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.manager.ItemListManager
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.util.format
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field
import java.util.*

class GiftBoxItem(
    var sender: Int = -1,
    var id: Int = -1,
    var contents: Array<ItemStack>,
    var date: Date = Date()
) {

    companion object {
        private const val url = "http://textures.minecraft.net/texture/47e55fcc809a2ac1861da2a67f7f31bd7237887d162eca1eda526a7512a64910";
        val giftBase: ItemStack by lazy {
                val head = ItemStack(Material.PLAYER_HEAD, 1)
                val headMeta = head.itemMeta
                val profile = GameProfile(UUID.randomUUID(), null)
                val encodedData =
                    Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).toByteArray())
                profile.properties.put("textures", Property("textures", String(encodedData)))
                var profileField: Field? = null
                try {
                    profileField = headMeta.javaClass.getDeclaredField("profile")
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                assert(profileField != null) { "" }
                profileField!!.isAccessible = true

                try {
                    profileField[headMeta] = profile
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                head.itemMeta = headMeta
                head
            }
    }

    val size by lazy {
        var count = 0
        contents.forEach { if(it != null && it.type != Material.AIR) count++ }
        count
    }

    val icon by lazy {
        val item = giftBase.clone()
        val meta = item.itemMeta
        meta.setDisplayName("§e선물 상자 §f[ §7${date.format()} §f]")
        meta.lore = arrayListOf(
            "",
            "§f아이템 : §a"+size+" 개",
            "§f보낸이 : §e"+ DSCoreAPI.getCustomName(sender),
            "",
            "§f§l[ §f클릭 §f§l] §a아이템 받기"
        )
    }

    fun gettingItem(p: Player): Boolean {
        if(size == 0) {
            p.sendMessage("§c선물 아이템 정보 읽기에 실패하였습니다.")
            return false
        }

        var empty = 0
        val inv = p.inventory
        for(i in 0 until 36) {
            val item = inv.getItem(i)
            if(item?.type?.equals(Material.AIR) != false) empty++
        }

        return if(size > empty) {
            p.sendMessage("§c인벤토리에 공간이 부족합니다.")
            p.sendMessage("§7 -> §a§l§n${size-empty}§7 칸을 더 비워야합니다.")
            false
        } else {
            contents.forEach { if(it != null && it.type != Material.AIR) inv.addItem(it) }
            remove()
            true
        }
    }

    private fun remove() {
        if(id == -1) return
        MysqlManager.executeQuery("DELETE FROM giftbox WHERE id=${id}")
    }

    fun send(p: Player, vararg targets: Int) {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            repeating(10)
            val items = ItemListManager.itemStackArrayToBase64(null,contents)
            val time = Date().time
            targets.forEach {
                send(it, time, items)
                DSCoreAPI.sendBungeeMessage(p, it, "§7선물이 도착했습니다. §f[ §a/우편함 §f]")
            }
        }

    }

    private fun send(target: Int, time: Long, items: String) {
        MysqlManager.executeQuery("INSERT INTO giftbox (player,sender,contents,date) " +
                "values ($target, $sender, '$items', $time)")
    }

}