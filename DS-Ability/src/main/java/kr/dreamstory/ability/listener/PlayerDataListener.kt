package com.dreamstory.ability.listener

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.boxs.GiftBox
import kr.dreamstory.ability.ability.play.boxs.PostBox
import kr.dreamstory.ability.ability.play.skills.cool.CoolTimeData
import kr.dreamstory.ability.api.DSCoreAPI
import com.dreamstory.ability.extension.*
import com.dreamstory.ability.manager.*
import com.dreamstory.ability.manager.ItemListManager.itemStackArrayFromBase64
import com.dreamstory.ability.util.DSLocation
import com.dreamstory.library.coroutine.SynchronizationContext
import com.dreamstory.library.coroutine.schedule
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class PlayerDataListener: Listener {

    val server = main.server
    private val scheduler by lazy { server.scheduler }
    private val waitingSet = HashSet<UUID>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerMoveEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: InventoryOpenEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerDropItemEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerAttemptPickupItemEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: AsyncChatEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerInteractEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: PlayerInteractAtEntityEvent) { if(waitingSet.contains(e.player.uniqueId)) e.isCancelled = true }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCheck(e: EntityDamageEvent) { if(waitingSet.contains(e.entity.uniqueId)) e.isCancelled = true }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        e.quitMessage(null)
        val p = e.player
        if(DSCoreAPI.getOnlinePlayersLocal().contains(p.id)) {
            MysqlManager.executeQuery("UPDATE server SET players='${DSCoreAPI.getOnlinePlayersLocal().toJson()}' WHERE port=${ChannelManager.port}")
            DSCoreAPI.removeOnlinePlayerLocal(p.id, p.wforestName)
            p.updateSql()
            p.ability?.quit()
            LogManager.saveLog(p, LogManager.LogType.QUIT)
            SkillManager.coolTimeSave(p)
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage(null)
        val p = e.player
        waitingSet.add(p.uniqueId)
        scheduler.schedule(main, SynchronizationContext.ASYNC) {
            p.sendMessage("")
            p.sendMessage("§7데이터를 불러오고 있습니다...")
            val id = p.id
            repeating(100)
            if(id <= 0) {
                val item = ItemStack(Material.PLAYER_HEAD)
                val meta = item.itemMeta as SkullMeta
                meta.owningPlayer = p
                item.itemMeta = meta

                MysqlManager.executeQuery("INSERT INTO player (uuid,name,inv,last_location,last_connect,op,hp,maxHp,hungry,ban,head_item,real_name,dungeon,port) values (" +
                        "'${p.uniqueId}', " +
                        "'${p.name}', " +
                        "'${ItemListManager.toBase64(p.inventory)}', " +
                        "'${locationToDSLocationString(p.location)}', " +
                        "${Date().time}, " +
                        "${p.isOp}, " +
                        "${p.health} ," +
                        "${p.maxHealth}, " +
                        "${p.foodLevel}, " +
                        "${p.isBanned}, " +
                        "'${ItemListManager.itemStackArrayToBase64(item)}', " +
                        "'${p.name}', " +
                        "3, " +
                        "${ChannelManager.port})")
                waitFor(1)
                DSCoreAPI.addOnlinePlayerLocal(p.id, p.name)
                switchContext(SynchronizationContext.SYNC)
                p.gameMode = GameMode.ADVENTURE
                p.sendMessage("§eNew Join!")
                p.sendMessage("")
                waitingSet.remove(p.uniqueId)
                MysqlManager.executeQuery("UPDATE server SET players='${DSCoreAPI.getOnlinePlayersLocal().toJson()}' WHERE port='${ChannelManager.port}'")
                return@schedule
            }
            val map: HashMap<String,Any> = MysqlManager.executeQuery("player","id",id)
            if(map.isNotEmpty()) {
                if(map["ban"] == true) {
                    switchContext(SynchronizationContext.SYNC)
                    waitingSet.remove(p.uniqueId)
                    p.kick(Component.text("§c서버에 접속할 수 없습니다.\n§7[ 관리자에게 문의하세요. ]"))
                    return@schedule
                }

                switchContext(SynchronizationContext.SYNC)
                val dest: DSLocation? = (map["dest_location"] as String).parseDSLocation(true)
                MysqlManager.executeQuery("UPDATE player SET dest_location='none' WHERE id=$id")
                val access: Boolean =
                    if(dest != null) p.teleport(dest)
                    else {
                        val loc = (map["last_location"] as String).parseDSLocation()
                        p.teleport(loc!!)
                    }

                if(!access) {
                    waitingSet.remove(p.uniqueId)
                    return@schedule
                }

                switchContext(SynchronizationContext.ASYNC)
                val name = map["name"] as String
                val inv = map["inv"] as String

                // save log data

                switchContext(SynchronizationContext.SYNC)
                p.inventory.contents = itemStackArrayFromBase64(inv)
                p.isOp = map["op"] as Boolean
                p.maxHealth = map["maxHp"] as Double
                p.health = map["hp"] as Double
                p.healthScale = 20.0
                p.foodLevel = map["hungry"] as Int
                p.wforestName = name
                val coolTimeData = CoolTimeData(id)
                val data: String? = MysqlManager.executeQuery("cool_times","cool_times","player",id)
                if(data != null) {
                    coolTimeData.coolTimes = data.fromJson()
                    coolTimeData.apply(p)
                }

                switchContext(SynchronizationContext.ASYNC)
                DSCoreAPI.addOnlinePlayerLocal(id, name)
                MysqlManager.executeQuery("UPDATE server SET players='${DSCoreAPI.getOnlinePlayersLocal().toJson()}' WHERE port='${ChannelManager.port}'")

                var label = ""
                val box = GiftBox.getGiftBox(id)
                if(box != null) {
                    label += "§e선물(§a${box.size}§e)§7 "
                }

                val box2 = PostBox.getPostBox(id)
                if(box2 != null) {
                    val i = box2.size
                    label += "§6초대(§a$i§6)§7 "
                }

                p.sendMessage("§e모든 데이터를 불러왔습니다.")
                p.sendMessage("")
                waitingSet.remove(p.uniqueId)
                LogManager.saveLog(p.uniqueId,inv, LogManager.LogType.JOIN)
                if(label != "") p.sendMessage("§7확인하지 않은 $label(이)가 있습니다.")

                val item = ItemStack(Material.PLAYER_HEAD)
                val meta = item.itemMeta as SkullMeta
                meta.owningPlayer = p
                item.itemMeta = meta
                MysqlManager.executeQuery("UPDATE player SET head_item='${ItemListManager.itemStackArrayToBase64(item)}', port=${ChannelManager.port} WHERE id=$id")
            } else {
                waitingSet.remove(p.uniqueId)
                switchContext(SynchronizationContext.SYNC)
                p.kick(Component.text("§6데이터를 불러오는데 실패하였습니다!\n§e( 재접속 부탁드립니다. )"))
            }
        }
    }

}