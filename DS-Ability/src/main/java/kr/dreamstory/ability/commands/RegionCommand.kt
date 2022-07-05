package kr.dreamstory.ability.commands

import kr.dreamstory.ability.ability.play.region.RegionGUI
import com.dreamstory.ability.extension.cutLabel
import kr.dreamstory.ability.manager.RegionManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class RegionCommand: CommandExecutor, TabCompleter {

    companion object {
        val commands = arrayListOf("생성","관리","삭제","목록")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if(sender !is Player) return false
        val p = sender as Player
        if(!p.isOp) return false

        if(args.isEmpty()) {
            p.sendMessage("§e/지역 생성 < wg_name > < name > : §f현 위치가 스폰장소로 설정 됩니다.")
            p.sendMessage("§e/지역 관리 < wg_name > : §f해당 이름의 지역 설정창을 엽니다.")
            p.sendMessage("§e/지역 삭제 < wg_name > : §f해당 이름의 지역을 삭제합니다.")
            p.sendMessage("§e/지역 목록 : §f생성 된 지역을 확인합니다.")
            return true
        }

        when(args[0]) {
            "생성"-> {
                execute(p,"§e/지역 생성 < wg_name > < name > : §f현 위치가 스폰장소로 설정 됩니다.") {
                    if(RegionManager.registerRegion(args[1],args.cutLabel(2),p)) p.sendMessage("§a생성 되었습니다.")
                    else p.sendMessage("§c생성 실패")
                }
            }
            "삭제"-> {
                execute(p, "§e/지역 삭제 < wg_name > : §f해당 이름의 지역을 삭제합니다.") {
                    val region = RegionManager.getRegion(args[1]) ?: run { p.sendMessage("§c해당 지역을 찾을 수 없습니다."); return false }
                    RegionManager.unregisterRegion(region)
                    p.sendMessage("§a삭제 되었습니다.")
                }
            }
            "관리"-> {
                execute(p, "§e/지역 관리 < wg_name > : §f해당 이름의 지역 설정창을 엽니다.") {
                    val reg = RegionManager.getRegion(args[1])
                    if (reg == null) {
                        p.sendMessage("§c찾을 수 없는 지역명입니다.")
                        return false
                    }
                    RegionGUI(reg, null).open(p)
                }
            }
            "목록"-> {
                var page = try { args[1].toInt() } catch (e: Exception) { 0 }
                val list = RegionManager.regionList
                var lastPage = list.size / 10
                if(list.size % 10 == 0) lastPage--
                if(lastPage < 0) lastPage = 0
                if(page > lastPage) page = lastPage
                if(page < 0) page = 0
                p.sendMessage("§e지역 목록 :: §6< §a$page §6/ §c$lastPage §6>")
                for(i in 0 until 10) {
                    val index = i + (page * 10)
                    if(list.size <= index) break
                    p.sendMessage("§e${index+1}. §f${list[i]}")
                }
            }
        }
        return true
    }

    private inline fun execute(p: Player, err: String, exe: ()->Unit) { try { exe(); } catch (e: Exception) { e.printStackTrace(); p.sendMessage(err); } }

    override fun onTabComplete(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): MutableList<String> {
        if(sender !is Player) return ArrayList()
        if(!sender.isOp) return ArrayList()
        return if(args.size <= 1) StringUtil.copyPartialMatches(args[0], commands , ArrayList())
        else if((args[0] == "삭제" || args[0] == "관리") && args.size <= 2) StringUtil.copyPartialMatches(args[1], RegionManager.regionList , ArrayList())
        else ArrayList()
    }

}