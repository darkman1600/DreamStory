package kr.dreamstory.community.chat

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.ability.extension.region
import kr.dreamstory.community.main
import kr.dreamstory.community.prefix.Prefix
import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.extension.database
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import world.bentobox.bentobox.BentoBox
import java.util.UUID

class CommunityData(uuid: UUID): ChatRenderer {

    var chatMode = ChatMode.DEFAULT
        private set
    fun setChatMode(mode: ChatMode) { chatMode = mode }

    var prefixList = HashSet<Prefix>()
    var prefix: Prefix? = Prefix("§6앙기모링")

    val ignorePlayerList = HashSet<UUID>()
    val ignoreMeList = HashSet<UUID>()
    val friends = HashSet<UUID>()

    fun checkIgnored(uuid: UUID): Boolean = ignorePlayerList.contains(uuid)
    fun addIgnore(uuid: UUID) { ignorePlayerList.add(uuid) }
    fun removeIgnore(uuid: UUID) { ignorePlayerList.remove(uuid) }

    init {
        loadData(uuid)
    }

    private fun loadData(uuid: UUID) {
        val data = PlayerDataManger.getOfflinePlayerData(uuid)
        val pList = data.getStringList(main,"prefix")
        if(pList.isNotEmpty()) pList.forEach { prefixList.add(Prefix(it)) }

        val modeTag = data.getStringOrNull(main,"chat_mode") ?: "default"
        val mode = ChatMode.fromString(modeTag.toUpperCase()) ?: ChatMode.DEFAULT
        setChatMode(mode)

        val igList = data.getStringList(main,"ignore_players")
        igList.forEach { ignorePlayerList.add(UUID.fromString(it)) }

        val igMeList = data.getStringList(main,"ignoreMe_players")
        igMeList.forEach { ignoreMeList.add(UUID.fromString(it)) }

        val fList = data.getStringList(main,"friends")
        fList.forEach { friends.add(UUID.fromString(it)) }
    }

    fun filterViewers(sender: Player): MutableSet<out Audience> {
        val newSet = mutableSetOf<Player>()
        when(chatMode) {
            ChatMode.DEFAULT -> newSet.addAll(Bukkit.getOnlinePlayers())
            ChatMode.ISLAND -> {
                val bb = BentoBox.getInstance()
                val members = bb.islands.getMembers(Bukkit.getWorld("bskyblock_world")!!,sender.uniqueId)
                val newList = mutableSetOf<Audience>()
                members.forEach {
                    val player = Bukkit.getPlayer(it)
                    if(player != null) newList.add(player)
                }
                return newList
            }
            ChatMode.REGION -> {
                val region = sender.region ?: return newSet
                for(uuid in region.players) {
                    newSet.add(Bukkit.getPlayer(uuid) ?: continue)
                }
            }
            ChatMode.FRIENDS -> {
                val fList = friends.mapNotNull { Bukkit.getPlayer(it) }
                newSet.add(sender)
                newSet.addAll(fList)
            }
        }
        newSet.removeAll {
            CommunityManager.getState(it.uniqueId).checkIgnored(sender.uniqueId)
        }
        return newSet
    }

    private fun AsyncChatEvent.chatEvent() {
        val filter = filterViewers(player)
        viewers().clear()
        viewers().addAll(filter)
        if(viewers().size <= 1) {
            isCancelled = true
            player.sendMessage(Component.text("메세지를 수신할 대상이 없습니다."))
        }
        viewers().add(Bukkit.getConsoleSender())
        renderer(this@CommunityData)
    }

    fun onDSChat(event: AsyncChatEvent) { event.chatEvent() }

    override fun render(player: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val playerData = player.database
        val perm = playerData.permission
        val permDisplay = Component.text(perm.font)

        val currentPrefix = prefix?.display
        val prefixDisplay = if(currentPrefix == null) Component.text("") else Component.text("§7[$currentPrefix§7]\uF822")

        val playerInfo: Component = Component.text("${playerData.uuid}")
            .append(prefixDisplay)
            .append(Component.text("§7클릭 시 귓속말을 보냅니다."))
        val playerName = player.displayName()
            .hoverEvent(HoverEvent.showText(playerInfo))
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/귓속말 ${player.name} "))

        val colorMessage = message.color(chatMode.color)

        val format = Component.text()
        format.append(Component.text("\uF802"))
        format.append(permDisplay)
        format.append(Component.text("\uF822"))
        format.append(prefixDisplay)
        format.append(playerName)
        format.append(Component.text("\uF822:\uF822"))
        format.append(colorMessage)

        //if(viewer is Player) format.append(Component.text(viewer.name))

        return format.build()
    }

}