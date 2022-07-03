package kr.dreamstory.ability.ability.play.skills.farm

import kr.dreamstory.ability.ability.play.skills.Active
import kr.dreamstory.ability.ability.play.skills.Skill
import com.dreamstory.ability.manager.CommandManager
import com.dreamstory.ability.util.doubleFormat
import com.dreamstory.ability.util.integerFormat
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class FarmActive : Skill(50), Active {

    private val name: String = "§6수확의 낫"
    private val coolTime = 59.8
    private val scale = 10
    private val range: Double = 3.0
    private val lore: ArrayList<String>
    private val upName: String
    private val upLore: ArrayList<String>

    init {
        lore = arrayListOf(
                "§f거대한 낫을 휘둘러 범위 내의 채집물을 모두 채집합니다.",
                "",
                "§7- 범위 : §e§l§o{range}",
                "§7- 재사용 대기 시간 : §c§l§o{cool}초",
                "",
                "§f스킬 레벨당 재사용 대기 시간 §c§l§o0.2초§f 감소",
                "",
                "§6{10}1차 강화 - 범위 : §e{10}§l§o3.5",
                "§6{20}2차 강화 - 범위 : §e{20}§l§o4",
                "§6{30}3차 강화 - 범위 : §e{30}§l§o4.5",
                "§6{40}4차 강화 - 범위 : §e{40}§l§o5",
                "§7각성 ( 원형 베기 ) - 스킬을 원형으로 사용합니다.",
                "",
                "§b좌클릭 - 스킬 포인트 투자",
                "",
                "§e우클릭 - 커맨드 설정 §7( 현재 커맨드 : §f{command} §7)"
        );

        upName = "§d원형 베기";
        upLore = arrayListOf(
                "§f거대한 낫을 두 번 휘둘러 범위 내의 채집물을 모두 채집합니다.",
                "",
                "§7- 범위 : §e§l§o5",
                "§7- 재사용 대기 시간 : §c§l§o50초",
                "",
                "§e우클릭 - 커맨드 설정 §7( 현재 커맨드 : §f{command} §7)"
        );
    }

    override fun getName(level: Int): String =
        if (level == 50) "$upName [ §5각성 스킬 §d]" else "$name [ §e§l§o$level LV §6]"

    override fun getSimpleName(level: Int): String = if (level == 50) upName else name

    override fun getIcon(level: Int, command: Int): ItemStack {
        val lore: MutableList<String> = java.util.ArrayList()
        var cmd: String = CommandManager.getCommandText(command)
        if (cmd == "") cmd = "§7없음"
        val name: String = getName(level)
        val des: List<String> = if (level == 50) upLore else this.lore

        val range = getRange(level)
        val cool = getCoolTime(level) / 20.0

        val coolTime: String =
            if (cool == (cool.toInt()).toDouble()) (cool.toInt()).integerFormat()
            else cool.doubleFormat()

        des.forEach {
            lore.add(
                    it.replace("{range}", range.toString())
                            .replace("{cool}", coolTime)
                            .replace("{10}", if (level >= 10) "" else "§7").replace("{20}", if (level >= 20) "" else "§7")
                            .replace("{30}", if (level >= 30) "" else "§7").replace("{40}", if (level >= 40) "" else "§7")
                            .replace("{command}", cmd)
            )
        }

        val item = ItemStack(Material.TNT)
        if (level == 50) item.addUnsafeEnchantment(Enchantment.LURE, 1)
        val meta = item.itemMeta
        meta.setDisplayName(name)
        meta.lore = lore
        if (level == 50) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    override fun getCoolTimeIcon(): Material = Material.MUSIC_DISC_CAT

    override fun getCoolTime(level: Int): Double {
        //val num1 = BigDecimal(coolTime)
        //val num2 = BigDecimal(level)
        //val num3 = BigDecimal("0.2")
        //return num1.subtract(num2.multiply(num3)).toDouble() * 20
        return 0.1
    }

    override fun action(level: Int, player: Player): Boolean {
        player.sendMessage("채집 액티브, 설정해야됨.")
        return true
    }

    private fun getScale(level: Int): Int {
        var scale = scale
        if (level >= 40) scale = 14 else if (level >= 30) scale = 13 else if (level >= 20) scale = 12 else if (level >= 10) scale = 11
        return scale
    }

    private fun getRange(level: Int): Double {
        var range = range
        if (level >= 40) range = 6.0 else if (level >= 30) range = 5.0 else if (level >= 20) range = 4.5 else if (level >= 10) range = 4.0
        return range
    }

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
}