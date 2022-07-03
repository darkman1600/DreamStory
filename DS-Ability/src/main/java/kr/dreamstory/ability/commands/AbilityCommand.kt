package kr.dreamstory.ability.commands

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.AbilityType
import com.dreamstory.ability.extension.updateSQL
import com.dreamstory.ability.manager.AbilityManager
import com.dreamstory.ability.manager.MysqlManager
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class AbilityCommand:CommandExecutor, TabCompleter {
    override fun onCommand(p: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(p !is Player) return false
        if(!p.isOp) return false

        if(args.isEmpty()) {
            p.sendMessage("§e/특성관리 [닉네임] : §f해당 유저의 특성을 조회합니다.")
            p.sendMessage("§e/특성관리 [닉네임] 레벨변경 [특성] [레벨] : §f해당 유저의 특성 레벨을 변경합니다.")
            p.sendMessage("§e/특성관리 [닉네임] 경험치추가 [특성] [경험치] : §f해당 유저의 특성 경험치를 추가합니다.")
            p.sendMessage("§e/특성관리 [닉네임] 초기화 : §f해당 유저의 특성을 초기화합니다.")
            return true
        }

        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            val target = main.server.getPlayer(args[0])
            var offline = false
            val id: Int? = MysqlManager.executeQuery("player", "id", "name", args[0])
            if (id == null) {
                p.sendMessage("§c해당 유저를 찾을 수 없습니다.")
                return@schedule
            }


            val ab = AbilityManager.getTempAbility(id)
            if (ab == null) {
                p.sendMessage("§c해당 유저의 특성 정보를 가져올 수 없습니다.")
                return@schedule
            }

            if (args.size == 1) {
                p.sendMessage("§b" + args[0] + " §f님의 특성 ::")
                p.sendMessage("§f - 채집(§e${ab.getLevel(AbilityType.FARM)}§f) : §a${ab.getExp(AbilityType.FARM)}")
                p.sendMessage("§f - 사냥(§e${ab.getLevel(AbilityType.HUNT)}§f) : §a${ab.getExp(AbilityType.HUNT)}")
                p.sendMessage("§f - 낚시(§e${ab.getLevel(AbilityType.FISH)}§f) : §a${ab.getExp(AbilityType.FISH)}")
                p.sendMessage("§f - 채광(§e${ab.getLevel(AbilityType.MINE)}§f) : §a${ab.getExp(AbilityType.MINE)}")
                return@schedule
            }

            when (args[1]) {
                "레벨변경" -> {
                    try {
                        val level = args[3].toInt()
                        val type: AbilityType = AbilityType.labelOf(args[2])
                        if (type == AbilityType.NONE) {
                            p.sendMessage("§c명령어가 잘못되었습니다.")
                            p.sendMessage("§7타입 종류 - 채집, 사냥, 낚시, 채광")
                            return@schedule
                        }
                        val suc = ab.setLevel(level, type)
                        if (suc) {
                            if (offline) ab.updateSQL()
                            p.sendMessage("§7해당 유저의 레벨을 §e${args[3]} §7(으)로 변경했습니다.")
                        } else {
                            p.sendMessage("§c레벨 값이 올바르지 않습니다.")
                        }
                    } catch (e: NumberFormatException) {
                        p.sendMessage("§c레벨 값은 숫자로 입력해야 합니다.")
                    } catch (e: Exception) {
                        p.sendMessage("§c명령어가 잘못되었습니다.")
                        p.sendMessage("§7타입 종류 - 채집, 사냥, 낚시, 채광")
                    }
                }
                "경험치추가" -> {
                    try {
                        val exp = args[3].toLong()
                        val type: AbilityType = AbilityType.labelOf(args[2])
                        if (type == AbilityType.NONE) {
                            p.sendMessage("§c명령어가 잘못되었습니다.")
                            p.sendMessage("§7타입 종류 - 채집, 사냥, 낚시, 채광")
                            return@schedule
                        }
                        val value = ab.addExp(exp, type)
                        if (value > 0) {
                            repeating(1)
                            var v = value
                            while (v > 0) {
                                v = ab.addExp(v, type)
                                yield()
                            }
                        }
                        if (offline) ab.updateSQL()
                        p.sendMessage("§7해당 유저의 경험치를 §e${args[3]} §7만큼 추가했습니다.")
                    } catch (e: NumberFormatException) {
                        p.sendMessage("§c경험치 값은 숫자로 입력해야 합니다.")
                    } catch (e: Exception) {
                        p.sendMessage("§c명령어가 잘못되었습니다.")
                        p.sendMessage("§7타입 종류 - 채집, 사냥, 낚시, 채광")
                    }
                }
                "초기화" -> {
                    ab.reset()
                    p.sendMessage("§c초기화 되었습니다.")
                }
            }
        }
        return false
    }

    private val com = listOf("레벨변경", "경험치추가", "초기화")

    override fun onTabComplete(sender: CommandSender, command: Command, s: String, args: Array<out String>): MutableList<String> {
        if (!sender.isOp) return ArrayList()
        return when(args.size) {
            2 -> { StringUtil.copyPartialMatches(args[1], com, ArrayList()) }
            3 -> { StringUtil.copyPartialMatches(args[2], AbilityType.getLabels(), ArrayList()) }
            else -> { ArrayList() }
        }
    }
}