package kr.dreamstory.ability.ability.play.block.obj

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class FishBuilder {
    private var power = 0.0
    private var name: String
    private var lore: MutableList<String>? = null
    private var material: Material? = null
    private var customModel = 0
    private var glow = false
    private var playerPower: Double
    private var temp: ItemStack? = null

    constructor(material: Material?) {
        this.material = material
        name = "§f물고기"
        power = 1.0
        playerPower = 1.5
    }

    constructor(item: ItemStack?) {
        temp = item
        playerPower = 1.5
        name = if (temp!!.itemMeta.hasDisplayName()) temp!!.itemMeta.displayName else temp!!.i18NDisplayName!!
    }

    fun setPower(power: Double): FishBuilder {
        this.power = power
        return this
    }

    fun setName(name: String): FishBuilder {
        this.name = name
        return this
    }

    fun setLore(lore: MutableList<String>?): FishBuilder {
        this.lore = lore
        return this
    }

    fun setPlayerPower(playerPower: Double): FishBuilder {
        this.playerPower = playerPower
        return this
    }

    fun addLore(line: String): FishBuilder {
        if (lore == null) lore = ArrayList()
        lore!!.add(line)
        return this
    }

    fun setGlow(glow: Boolean): FishBuilder {
        this.glow = glow
        return this
    }

    fun setCustomModel(customModel: Int): FishBuilder {
        this.customModel = customModel
        return this
    }

    fun build(exp: Long): Fish {
        val item: ItemStack
        if (temp != null) item = temp!!
        else {
            item = ItemStack(material!!)
            val meta = item.itemMeta
            meta.setDisplayName(name)
            if (lore != null) meta.lore = lore
            meta.setCustomModelData(customModel)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
            item.setItemMeta(meta)
            if (glow) item.addUnsafeEnchantment(Enchantment.LURE, 1)
        }
        return Fish(item, power, playerPower, exp)
    }
}