package kr.dreamstory.ability.commands.island

import kr.dreamstory.ability.ability.play.island.gui.IslandMainGUI
import com.dreamstory.ability.extension.changeChannel
import com.dreamstory.ability.extension.id
import com.dreamstory.ability.extension.island
import com.dreamstory.ability.manager.DSIslandManager
import com.dreamstory.ability.manager.MysqlManager
import org.bukkit.entity.Player

@Deprecated(message = "추가 금지", level = DeprecationLevel.WARNING)
object IslandCommand {

    fun printHelpLine(p: Player) {
        p.sendMessage("§e/섬 §f: 섬으로 이동합니다. §7( 섬 서버에서만 가능합니다. )")
        p.sendMessage("§e/섬 메뉴 §f: 섬 메뉴를 엽니다.")
        p.sendMessage("§e/섬 초대 < 닉네임 > §f: 섬원을 초대합니다.")
        p.sendMessage("§e/섬 추방 < 닉네임 > §f: 섬원을 내보냅니다.")
        p.sendMessage("§e/섬 알바초대 < 닉네임 > §f: 섬 알바를 초대합니다.")
        p.sendMessage("§e/섬 알바종료 < 닉네임 > §f: 섬 알바를 종료시킵니다.")
        p.sendMessage("§e/섬 양도 < 닉네임 > §f: 섬을 양도합니다. §7( 섬 서버에서만 가능합니다. )")
        p.sendMessage("§e/섬 내쫓기 < 닉네임 > §f: 섬에 있는 유저를 내쫓습니다. §7( 섬 서버에서만 가능합니다. )")
        p.sendMessage("§c/섬 떠나기 §f: 소속된 섬을 떠납니다.")
        p.sendMessage("§c/섬 삭제 §f: 섬을 삭제합니다. §7( 섬 서버에서만 가능합니다. )")
    }

    fun onCommand(p: Player, args: Array<String>) {
        if(args.isEmpty()) {
            val island = p.island
            if(island == null) {
                // 섬 없는 경우
                val serverName = DSIslandManager.getIslandNextMakeServer()
                if(serverName == null) {
                    p.sendMessage("§c섬 서버에 접속할 수 없습니다.")
                    return
                } else {
                    MysqlManager.executeQuery("UPDATE player SET dest_location='$serverName : NULL' WHERE id=${p.id}")
                    p.changeChannel(serverName)
                }
            } else {
                // 섬 있는 경우
                island.gotoIsland(p)
            }
            return
        }

        if(args[0] == "명령어" || args[0] == "도움말" || args[0] == "?") {
            printHelpLine(p)
            return
        }

        val island = p.island
        if(island == null) {
            p.sendMessage("§c가입 된 섬이 없습니다.")
            return
        }

        when(args[0]) {
            "메뉴"-> {
                IslandMainGUI(p,island)
            }
        }
    }

    fun onTabComplete(p: Player, args: Array<String>): ArrayList<String> {
        return arrayListOf()
    }

}