package kr.dreamstory.community.whisper

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.main
import kr.dreamstory.library.DSLibraryAPI
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.data.PlayerDataManger
import org.bukkit.entity.Player

object WhisperManager {
    fun whisper(p: Player, targetName: String) {
        main.schedule(SynchronizationContext.ASYNC) {
            val t = DSLibraryAPI.getPlayerExact(targetName)
            if(t == null) {
                p.sendMessage("해당 플레이어를 찾을 수 없습니다.")
                return@schedule
            }
            val pUUID = p.uniqueId
            val targetUUID = t.uniqueId
            val tcd = CommunityManager.getCommunityData(targetUUID)
            if(tcd == null) {
                p.sendMessage("알 수 없는 오류로 실패하였습니다. 다시 시도하세요.")
                return@schedule
            }
            if(tcd.ignoreList.contains(pUUID)) {
                p.sendMessage("귓속말이 차단되었습니다.")
                return@schedule
            }
            if(!PlayerDataManger.getPlayerData(targetUUID)!!.getBoolean("option.whisper_toggle")) {
                p.sendMessage("해당 유저는 귓속말을 비활성화 하였습니다.")
                return@schedule
            }
        }
    }
}