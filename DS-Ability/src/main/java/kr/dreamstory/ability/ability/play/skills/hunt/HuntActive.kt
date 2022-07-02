package kr.dreamstory.ability.ability.play.skills.hunt

import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.block.AbilityObject
import kr.dreamstory.ability.ability.play.block.MineObject
import kr.dreamstory.ability.ability.play.skills.Active
import kr.dreamstory.ability.ability.play.skills.Skill
import com.dreamstory.ability.manager.AbilityBlockManager
import com.dreamstory.ability.manager.CommandManager
import com.dreamstory.ability.util.doubleFormat
import com.dreamstory.ability.util.integerFormat
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.math.BigDecimal

class HuntActive : Skill(50), Active {

    private val head by lazy {
        val temp = ItemStack(Material.TNT)
        val meta: ItemMeta = temp.itemMeta
        meta.setCustomModelData(1)
        temp.itemMeta = meta
        temp
    }
    private val name: String = "§6폭탄 채굴"
    private val coolTime: Double = 59.8
    private val range: Int
    private val lore: ArrayList<String>
    private val upName: String
    private val upLore: ArrayList<String>

    init {
        range = 5;
        lore = arrayListOf(
                "§f폭탄을 터뜨려 범위 내의 광물을 모두 채굴합니다.",
                "",
                "§7- 범위 : §e§l§o{range}",
                "§7- 설치 후 폭발 시간 : §a§l§o{time}초",
                "§7- 재사용 대기 시간 : §c§l§o{cool}초",
                "",
                "§f스킬 레벨당 재사용 대기 시간 §c§l§o0.2초§f 감소",
                "",
                "§6{10}1차 강화 - 폭발 시간 §a{10}§l§o2.5초",
                "§6{20}2차 강화 - 폭발 시간 §a{20}§l§o2초§6{20}, 폭발 반경 §e{20}§l§o( 7x7 )",
                "§6{30}3차 강화 - 폭발 시간 §a{30}§l§o1.5초",
                "§6{40}4차 강화 - 폭발 시간 §a{40}§l§o1초§6{40}, 폭발 반경 §e{40}§l§o( 9x9 )",
                "§7각성 ( 던지기 ) - 스킬 사용 시 폭탄을 전방으로 던짐",
                "",
                "§b좌클릭 - 스킬 포인트 투자",
                "",
                "§e우클릭 - 커맨드 설정 §7( 현재 커맨드 : §f{command} §7)"
        );

        upName = "§d폭탄 던지기";
        upLore = arrayListOf(
                "§f폭탄을 터뜨려 범위 내의 광물을 모두 채굴합니다.",
                "§7( 폭탄은 블럭에 닿는 즉시 터집니다. )",
                "§c* 폭탄을 던진 후 10초간 블럭에 닿지 않으면 즉시 터집니다.",
                "",
                "§7- 범위 : §e§l§o9x9",
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

        val bombTime: String =
                if (level >= 40) "1" else if (level >= 30) "1.5" else if (level >= 20) "2" else if (level >= 10) "2.5" else "3"

        des.forEach {
            lore.add(
                    it.replace("{range}", range.toString() + "x" + range).replace("{time}", bombTime)
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

    override fun getCoolTimeIcon(): Material = Material.MUSIC_DISC_BLOCKS

    override fun getCoolTime(level: Int): Double {
        val num1 = BigDecimal(coolTime)
        val num2 = BigDecimal(level)
        val num3 = BigDecimal("0.2")
        return num1.subtract(num2.multiply(num3)).toDouble() * 20
    }

    override fun action(level: Int, player: Player): Boolean {
        player.sendMessage("사냥 액티프, 설정해야됨.")
        return true
    }

    private fun getRange(level: Int): Int {
        var range = range
        if (level >= 40) range = 9 else if (level >= 20) range = 7
        return range
    }

    private fun getBombTime(level: Int): Double =
            if (level >= 50) 10.0 else if (level >= 40) 1.0 else if (level >= 30) 1.5 else if (level >= 20) 2.0 else if (level >= 10) 2.5 else 3.0

    private fun bomb(p: Player, range: Int, ab: Ability, am: ArmorStand) {
        am.world.playSound(am.location, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1f)
        val blocks: List<Block> = getNearBlocks(am.location, range, true)
        am.remove()
        blocks.forEach { b: Block ->
            val abb: AbilityObject = AbilityBlockManager.getAbilityBlock(b.blockData) ?: return@forEach
            if (abb is MineObject) abb.breakBlock(p, b, ab, true)
        }
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