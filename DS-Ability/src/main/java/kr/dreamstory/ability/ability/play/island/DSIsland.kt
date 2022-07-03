package kr.dreamstory.ability.ability.play.island

import kr.dreamstory.ability.ability.play.island.data.DSIslandOption
import kr.dreamstory.ability.ability.play.island.data.IslandOptionType
import kr.dreamstory.ability.ability.play.island.data.IslandPermissionType
import com.dreamstory.ability.extension.*
import com.dreamstory.ability.manager.ChannelManager
import com.dreamstory.ability.manager.MysqlManager
import com.dreamstory.ability.util.DSLocation
import org.bukkit.entity.Player

class DSIsland(private val islandId: Int) {

    val owner: Int get() = MysqlManager.executeQuery("island","owner","id",islandId)!!
    val members: DSIslandMembers get() = DSIslandMembers(islandId, MysqlManager.executeQuerySet("player","id","island",islandId))
    val coops: DSIslandCoops get() = DSIslandCoops(islandId, MysqlManager.executeQuerySet("island_coop","player","island", islandId))
    val maxSize: Int get() = MysqlManager.executeQuery("island","member_size","id",islandId)?: 20
    val exp: Long get() {
        val tempExp:String = MysqlManager.executeQuery("island","exp","id", islandId)!!
        return tempExp.toLong()
    }

    var icon: String
        get() = MysqlManager.executeQuery("island","icon","id",islandId)!!
        set(value) { MysqlManager.executeQuery("UPDATE island SET icon='$value' WHERE id=$islandId") }

    val iconLabel: String
        get() {
            val icon = this.icon
            return if(icon.isBlank()) ""
            else "§f$icon§f "
        }

    val subOwners: HashSet<Int> get() {
        val data: String? = MysqlManager.executeQuery("island","sub_owners","id",islandId)
        return if(data.isNullOrBlank()) HashSet()
        else data.fromJson()
    }

    fun setSubOwner(playerId: Int): Boolean {
        if(!members.contains(playerId)) return false
        val set = subOwners
        if(set.contains(playerId)) return false
        set.add(playerId)
        MysqlManager.executeQuery("UPDATE island SET sub_owners='${set.toJson()}' WHERE id=$islandId")
        return true
    }

    val challengeCount: Int get() = MysqlManager.executeQuery("island","challenge","id",islandId)!!
    val nowExp: Long get() = exp % 1000
    val level: Long get() = exp / 1000
    val location: DSLocation? get() {
        val temp: String = MysqlManager.executeQuery("island","spawn","id",islandId)?: return null
        return temp.parseDSLocation(true)
    }

    val rank: Int get() {
        var result = -1
        val s = MysqlManager.connection!!.prepareStatement("SELECT exp, ( SELECT COUNT(*) + 1 FROM island WHERE exp > b.exp ) AS rank FROM island AS b WHERE id=$islandId")
        val set = s.executeQuery()
        if(set.next()) result = set.getInt("rank")
        set.close()
        s.close()
        return result
    }

    val name: String get() = MysqlManager.executeQuery("island","name","id",islandId)!!
    val ownerLabel: String get() {
        val s: String = MysqlManager.executeQuery("island","owner_name","id",islandId)!!
        return "§6$s"
    }
    val subOwnerLabel: String get() {
        val s: String = MysqlManager.executeQuery("island","sub_owner_name","id",islandId)!!
        return "§e$s"
    }
    val memberLabel: String get() {
        val s : String = MysqlManager.executeQuery("island","member_name","id",islandId)!!
        return "§f$s"
    }

    val option: DSIslandOption
        get() {
        val data: String = MysqlManager.executeQuery("island_option","option_data","island",islandId)!!
        return data.fromJson()
    }

    fun hasPermission(type: IslandPermissionType, playerId: Int): Boolean {
        if (!members.contains(playerId)) return false
        if (owner == playerId) return true
        var optionType: IslandOptionType? = null
        when (type) {
            IslandPermissionType.ICON -> optionType = option.icon
            IslandPermissionType.KICK -> optionType = option.kick
            IslandPermissionType.BOARD -> optionType = option.board
            IslandPermissionType.EXPELL -> optionType = option.expell
            IslandPermissionType.INVITE -> optionType = option.invite
            IslandPermissionType.NOTICE -> optionType = option.notice
            IslandPermissionType.CHALLENGE -> optionType = option.challenge
            IslandPermissionType.COOP_INVITE -> optionType = option.coop_invite
        }
        return if (optionType.index == 2) true else if(optionType.index == 1) subOwners.contains(playerId) || owner == playerId else owner == playerId
    }

    fun addMember(playerId: Int): Boolean = members.add(playerId)
    fun removeMember(playerId: Int): Boolean = members.remove(playerId)
    fun containsMember(playerId: Int): Boolean = members.contains(playerId)

    fun addCoop(playerId: Int): Boolean =if(members.contains(playerId)) false else coops.add(playerId)
    fun removeCoop(playerId: Int): Boolean = coops.remove(playerId)
    fun containsCoop(playerId: Int): Boolean = coops.contains(playerId)

    @Deprecated(level = DeprecationLevel.WARNING, message = "Island Delete!!!")
    fun removeIsland() {

    }

    fun gotoIsland(p: Player) {
        val location = location
        if(location == null) {
            p.sendMessage("§cERROR!")
        } else {
            if(ChannelManager.isMainServer) {
                MysqlManager.executeQuery("UPDATE player SET dest_location='${location}' WHERE id=${p.id}")
                p.changeChannel(location.islandServerName)
            } else {
                if(ChannelManager.name != location.islandServerName) {
                    MysqlManager.executeQuery("UPDATE player SET dest_location='${location}' WHERE id=${p.id}")
                    p.changeChannel(location.islandServerName)
                } else {
                    p.teleport(location.location!!)
                }
            }
        }
    }

}