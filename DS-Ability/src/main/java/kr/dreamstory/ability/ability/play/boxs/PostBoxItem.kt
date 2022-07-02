package kr.dreamstory.ability.ability.play.boxs

import kr.dreamstory.ability.ability.play.data.PlayerOption
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.extension.friends
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.extension.wforestName
import com.dreamstory.ability.manager.DSIslandManager
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.util.format
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.abs

class PostBoxItem(
    private var sender: Int = -1,
    var id: Int = -1,
    var type: PostBoxType,
    var date: Date = Date()
) {

    private val isSendAble get() = id == -1
    private val isContains: Boolean get() {
        val s = MysqlManager.connection!!.prepareStatement("SELECT COUNT(*) FROM postbox WHERE player=? and sender=? and type=?")
        val set = s.executeQuery()
        val result = set.next()
        set.close()
        s.close()
        return result
    }
    private val senderName: String get() {
        return when(type) {
            PostBoxType.ADD_FRIEND -> DSCoreAPI.getCustomName(sender)?:"§7알 수 없음"
            PostBoxType.JOIN_ISLAND -> DSIslandManager.getDSIsland(sender,false)?.name?: "§7알 수 없음"
            PostBoxType.COOP_ISLAND -> DSIslandManager.getDSIsland(sender,false)?.name?: "§7알 수 없음"
        }
    }

    val icon by lazy {
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta
        meta.setCustomModelData(type.customModelNumber)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
        meta.setDisplayName("§f${type.label}")
        meta.lore = arrayListOf(
            "",
            "§f대상 : $senderName",
            "§f일시 : §7${date.format()}",
            "",
            "§f§l[ §f클릭 §f§l] §a수락 §7/ §f§l[ §f쉬프트+클릭 §f§l] §c거절"
        )
        item.itemMeta = meta
        item
    }

    fun remove() {
        if(id == -1) return
        MysqlManager.executeQuery("DELETE FROM postbox WHERE id=$id")
    }

    fun deny(p: Player) {
        if(isSendAble) return
        p.sendMessage("§7거절 하였습니다.")
        remove()
    }

    fun accept(p: Player): Boolean {
        if(isSendAble) return false
        val acceptData = Date()
        val calDate = acceptData.time - date.time
        var calDateDays = calDate / 86400000L
        calDateDays = abs(calDateDays)
        if(calDateDays >= 1) {
            p.sendMessage("§c초대가 만료되었습니다.")
            remove()
            return false
        }

        when(type) {
            PostBoxType.ADD_FRIEND -> {
                remove()
                val friends = p.friends
                if(friends.contains(sender)) {
                    p.sendMessage("§c해당 유저와 이미 친구 상태입니다.")
                    return false
                }
                friends.add(sender)
                DSCoreAPI.sendBungeeMessage(p, sender, "§f${p.wforestName} §7님과 친구가 되었습니다.")
                p.sendMessage("§f${DSCoreAPI.getCustomName(sender)} §7님과 친구가 되었습니다.")
                return true
            }
            PostBoxType.JOIN_ISLAND -> {
                var island = DSIslandManager.getDSIsland(p.id, true)
                if(island != null) {
                    p.sendMessage("§c이미 가입 된 섬이 있습니다.")
                    remove()
                    return false
                }
                island = DSIslandManager.getDSIsland(sender, false)
                if(island == null) {
                    p.sendMessage("§c해당 섬을 찾을 수 없습니다.")
                    remove()
                    return false
                }
                val coopIsland: Int? = MysqlManager.executeQuery("island_coop","island","player",p.id)
                if(coopIsland != null || coopIsland != 0) {
                    p.sendMessage("§c알바중에는 다른 섬에 가입할 수 없습니다.")
                    remove()
                    return false
                }

                if(island.maxSize <= island.members.size) {
                    p.sendMessage("§c가입하려는 섬이 가득찼습니다.")
                    remove()
                    return false
                }
                island.members.add(p.id)
                val name = p.wforestName
                island.members.forEach { DSCoreAPI.sendBungeeMessage(p, it, "§f$name §7님이 섬에 가입하였습니다.") }
                remove()
                return true
            }
            PostBoxType.COOP_ISLAND -> {
                val islandId: Int? = MysqlManager.executeQuery("island_coop","player","player",p.id)
                if(islandId?.equals(0) != true) {
                    p.sendMessage("§c이미 다른 섬의 알바를 진행중입니다.")
                    remove()
                    return false
                }

                val island = DSIslandManager.getDSIsland(sender, false)
                if(island?.members?.contains(p.id) == true) {
                    p.sendMessage("§c가입 된 섬의 알바는 할 수 없습니다.")
                    remove()
                    return false
                }

                if(island == null) {
                    p.sendMessage("§c해당 섬을 찾을 수 없습니다.")
                    remove()
                    return false;
                }

                if(island.coops.size >= 5) {
                    p.sendMessage("§c해당 섬의 아르바이트 인원이 가득찼습니다.")
                    remove()
                    return false
                }

                island.addCoop(p.id)
                val name = p.wforestName
                p.sendMessage("§7섬 알바를 시작합니다. [ 일시 : §e${Date().format()} §7]")
                island.members.forEach { DSCoreAPI.sendBungeeMessage(p, it,"§f$name §7님이 섬 알바를 시작합니다.") }
                remove()
                return true
            }
            else -> return false
        }
    }

    fun send(p: Player,target: Int): Boolean {
        if(!isSendAble) return false
        val count: Int? = MysqlManager.executeQuery("postbox","COUNT(*)","player",target)
        if(count != null && count >= 27) {
            p.sendMessage("§c해당 유저의 초대함이 가득 찼습니다.")
            return false
        }
        val option = PlayerOption.getPlayerOptionById(target)
        if(option == null) {
            p.sendMessage("§c서버 오류로 인해, 초대를 보내지 못 했습니다.")
            return false
        }

        if(isContains) {
            p.sendMessage("§c이미 같은 초대를 보냈습니다.")
            return false
        }
        return when(type) {
            PostBoxType.ADD_FRIEND -> {
                p.sendMessage("§7친구 요청을 보냈습니다.")
                MysqlManager.executeQuery(("INSERT INTO postbox (player,sender,type,date) values ($target,$sender,${type.index},${Date().time})"))
                DSCoreAPI.sendBungeeMessage(p, target, "§7친구요청이 도착했습니다. §f[§a/우편함§f]");
                true
            }
            PostBoxType.JOIN_ISLAND -> {
                MysqlManager.executeQuery(("INSERT INTO postbox (player,sender,type,date) values ($target,$sender,${type.index},${Date().time})"))
                DSCoreAPI.sendBungeeMessage(p, target, "§7섬 초대가 도착했습니다. §f[§a/우편함§f]");
                true
            }
            PostBoxType.COOP_ISLAND -> {
                MysqlManager.executeQuery(("INSERT INTO postbox (player,sender,type,date) values ($target,$sender,${type.index},${Date().time})"))
                DSCoreAPI.sendBungeeMessage(p, target, "§7섬 알바 초대가 도착했습니다. §f[§a/우편함§f]");
                true
            }
            else -> {
                false
            }
        }
    }



}