package kr.dreamstory.community.chat

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import kr.dreamstory.ability.extension.region
import kr.dreamstory.community.prefix.Prefix
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.data.PlayerDataManger
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import world.bentobox.bentobox.BentoBox
import java.util.UUID

class CommunityData(playerData: PlayerData): ChatRenderer {

    var chatMode: ChatMode = ChatMode.DEFAULT; private set
    var prefix: Prefix? = null;                private set
    var prefixList = ArrayList<Prefix>();      private set
    var ignoreList = ArrayList<UUID>();        private set
    var ignoreMeList = ArrayList<UUID>();      private set
    var friends = ArrayList<UUID>();           private set

    init {
        chatMode = ChatMode.fromString(playerData.getStringOrNull("chat_mode") ?: "default") ?: ChatMode.DEFAULT
        val prefixTag = playerData.getStringOrNull("prefix")
        if(prefixTag != null) { prefix = Prefix(prefixTag) }
        playerData.getStringList("prefix_list").forEach { prefixList.add(Prefix(it)) }
        playerData.getStringList("ignore_list").forEach { ignoreList.add(UUID.fromString(it)) }
        playerData.getStringList("ignore_me_list").forEach { ignoreMeList.add(UUID.fromString(it)) }
        playerData.getStringList("friend_list").forEach { friends.add(UUID.fromString(it)) }
    }

    fun setChatMode(chatMode: ChatMode) { this.chatMode = chatMode }

    fun filterViewers(sender: Player): MutableSet<out Audience> {
        val newSet = mutableSetOf<Player>()
        when(chatMode) {
            ChatMode.DEFAULT -> newSet.addAll(DSLibraryAPI.dsOnlinePlayers)
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
        newSet.removeAll { ignoreMeList.contains(it.uniqueId) }
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
        val playerData = PlayerDataManger.getPlayerData(player.uniqueId) ?: return Component.text("error")
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