package kr.dreamstory.ability.ability.play.skills.mine

import kr.dreamstory.ability.ability.event.AbilityBlockBreakEvent
import kr.dreamstory.ability.ability.play.skills.Passive
import kr.dreamstory.ability.ability.play.skills.Skill
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MinePassive() : Skill(30), Passive {

    private val name: String = "§6피로 면역"
    private val lore: ArrayList<String> = arrayListOf(
        "§f채굴 시 입는 피해가 감소합니다.",
        "",
        "§7- 피해 감소량 : §e§l§o{res}%",
        "",
        "§f스킬 레벨당 피해 감소량 §e§l§o3%§f 증가",
    )
    private val res : Int = 3

    override fun getName(level: Int): String = if(level < 30) "$name [ §e§l§o$level LV §6]" else "§d피로 면역 §5[ §d최대 레벨 §5]"

    override fun getSimpleName(level: Int): String = name

    override fun getIcon(level: Int, command: Int): ItemStack {
        val lore: MutableList<String> = java.util.ArrayList()
        val name: String = getName(level)
        val des: List<String> = this.lore

        des.forEach {
            lore.add(
                it.replace("{res}", (res * level).toString())
            )
        }
        if(level < 30) {lore.addAll(listOf("","§b좌클릭 - 스킬 포인트 투자"))}

        val item = ItemStack(Material.GOLDEN_APPLE)
        val meta = item.itemMeta
        meta.setDisplayName(name)
        meta.lore = lore
        item.itemMeta = meta
        return item
    }
    override fun AbilityBlockBreakEvent.action(level: Int) {
        fatigue = level * res
    }
}