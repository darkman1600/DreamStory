package kr.dreamstory.ability.ability.play.skills.fish

import kr.dreamstory.ability.ability.play.skills.Active
import kr.dreamstory.ability.ability.play.skills.Skill
import kr.dreamstory.ability.manager.CommandManager
import kr.dreamstory.ability.util.doubleFormatScale2
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.math.BigDecimal

class FishBuff : Skill(40), Active {

    private val bottle by lazy {
        val temp = ItemStack(Material.TNT)
        val meta: ItemMeta = temp.itemMeta
        meta.setCustomModelData(6)
        temp.itemMeta = meta
        temp
    }
    private val cork by lazy {
        val temp = ItemStack(Material.TNT)
        val meta: ItemMeta = temp.itemMeta
        meta.setCustomModelData(7)
        temp.itemMeta = meta
        temp
    }
    private val name: String = "§6힘의 비약"
    private val activeTime: Double = 60.0
    private val power: Double = 0.05
    private val lore: ArrayList<String>
    private val upName: String
    private val upLore: ArrayList<String>

    init {
        lore = arrayListOf(
                "§f일정 시간동안 힘 ( 채광 ) 이 증가합니다.",
                "",
                "§7- 지속 시간 : §a§l§o{activeTime}초",
                "§7- 힘 ( 채광 ) : §e§l§o+{power}",
                "§7- 재사용 대기 시간 : §c§l§o150초",
                "",
                "§f스킬 레벨당 힘 §e§l§o0.05§f 증가",
                "",
                "§6{10}1차 강화 - 지속 시간 : §a{10}§l§o80초",
                "§6{20}2차 강화 - 지속 시간 : §a{20}§l§o100초",
                "§6{30}3차 강화 - 지속 시간 : §a{30}§l§o120초",
                "§7각성 ( 민첩함 ) - 스킬 사용 시 5초간 광물을 빠르게 채굴합니다.",
                "§7        5초 이후 성급함 버프 발동.",
                "",
                "§b좌클릭 - 스킬 포인트 투자",
                "",
                "§e우클릭 - 커맨드 설정 §7( 현재 커맨드 : §f{command} §7)"
        );

        upName = "§d끓어오르는 힘의 비약";
        upLore = arrayListOf(
                "§f일정 시간동안 힘 ( 채광 ) 이 증가합니다.",
                "§7( 스킬 사용 시 5초간 광물을 빠르게 채굴합니다.",
                "§7 이후에는 힘의 비약 스킬이 발동됩니다. )",
                "",
                "§7- 지속 시간 : §a§l§o120초",
                "§7- 힘 ( 채광 ) : §e§l§o+2",
                "§7- 재사용 대기 시간 : §c§l§o30초",
                "",
                "§e우클릭 - 커맨드 설정 §7( 현재 커맨드 : §f{command} §7)"
        );
    }

    override fun getName(level: Int): String =
            if (level == 40) "$upName [ §5각성 스킬 §d]" else "$name [ §e§l§o$level LV §6]"

    override fun getSimpleName(level: Int): String = if (level == 40) upName else name

    override fun getIcon(level: Int, command: Int): ItemStack {
        val lore: MutableList<String> = java.util.ArrayList()
        var cmd: String = CommandManager.getCommandText(command)
        if (cmd == "") cmd = "§7없음"
        val name: String = getName(level)
        val des: List<String> = if (level == 40) upLore else this.lore

        val activeTime = getActiveTime(level)
        val power = getPower(level)

        des.forEach {
            lore.add(
                    it.replace("{activeTime}", activeTime.toString()).replace("{power}", power.doubleFormatScale2())
                            .replace("{10}", if (level >= 10) "" else "§7").replace("{20}", if (level >= 20) "" else "§7")
                            .replace("{30}", if (level >= 30) "" else "§7").replace("{command}", cmd)
            )
        }

        val item = ItemStack(Material.POTION)
        if (level == 40) item.addUnsafeEnchantment(Enchantment.LURE, 1)
        val meta = item.itemMeta
        meta.setDisplayName(name)
        meta.lore = lore
        if (level == 40) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_POTION_EFFECTS)
        item.itemMeta = meta
        return item
    }

    override fun getCoolTime(level: Int): Double { return 3000.0 }

    override fun getCoolTimeIcon(): Material = Material.MUSIC_DISC_13

    override fun action(level: Int, player: Player): Boolean {
        player.sendMessage("낚시 버프, 설정해야됨.")
        return true
    }

    fun getPower(level: Int): Double {
        val num1 = BigDecimal(level)
        val num2 = BigDecimal(power)
        return num1.multiply(num2).toDouble()
    }

    fun getActiveTime(level: Int): Double =
            if (level >= 30) 120.0 else if (level >= 20) 100.0 else if (level >= 10) 80.0 else 60.0


    private fun getNearBlocks(loc: Location, range: Int, yCheck: Boolean): List<Block> {
        val r = range / 2
        val yy = if (yCheck) r else 0
        val bList: ArrayList<Block> = ArrayList()
        val lx = loc.blockX
        val ly = loc.blockY
        val lz = loc.blockZ
        for (x in r * -1..r) {
            for (y in yy * -1..yy) {
                for (z in r * -1..r) {
                    val b = loc.world.getBlockAt(lx + x, ly + y, lz + z)
                    if (b.type != Material.AIR) bList.add(b)
                }
            }
        }
        return bList
    }
    private class Animation(val pose: (am: ArmorStand, time: Double, p: Player) -> Unit) {
        fun setPose (am: ArmorStand, time: Int, p: Player) { pose(am, time.toDouble(), p) }
    }
}