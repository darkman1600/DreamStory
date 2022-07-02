package kr.dreamstory.ability.ability.play.block.obj

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.stream.Collectors

enum class MobType(var label: String, var des: List<String>, var icon: Material, var index: Int) {
    MOB(
        "§f일반 몹",
        Arrays.asList<String>("§f일반적으로 필드에 배치되는 몹 입니다.", "§f경험치와 아이템을 드랍합니다."),
        Material.ZOMBIE_HEAD,
        0
    ),
    DUNGEON_MOB(
        "§f던전 몹",
        Arrays.asList<String>("§f던전에서 생성되는 몹 입니다.", "§f경험치만 드랍합니다."),
        Material.SKELETON_SKULL,
        1
    ),
    BOSS1(
        "§c보스 몹 (1)",
        Arrays.asList<String>("§f데미지 순위 1~N 등에게만 적용되는 보스입니다.", "§f아이템을 드랍합니다."),
        Material.DRAGON_HEAD,
        2
    ),
    BOSS2(
        "§c보스 몹 (2)",
        Arrays.asList<String>("§f던전 입장한 모든 유저에게 적용되는 보스입니다.", "§f아이템을 드랍합니다."),
        Material.DRAGON_HEAD,
        3
    );

    val invIcon: ItemStack get() {
        val icon = ItemStack(this.icon)
        val meta = icon.itemMeta
        meta.setDisplayName(label)
        val loreClone: MutableList<String> = ArrayList(des)
        loreClone.add("")
        loreClone.add("§a클릭 시 변경됩니다.")
        meta.lore = loreClone
        icon.itemMeta = meta
        return icon
    }

    operator fun next(): MobType {
        var index = index
        ++index
        if (index >= 4) index = 0
        return indexOf(index)
    }

    companion object {
        fun indexOf(index: Int): MobType {
            return try {
                val type: MobType =
                    Arrays.stream(values()).filter { t: MobType -> t.index == index }.collect(Collectors.toList())[0] ?: return MOB
                type
            } catch (e: Exception) {
                MOB
            }
        }
    }
}