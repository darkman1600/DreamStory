package kr.dreamstory.library.permission

import kr.dreamstory.library.data.PlayerData
import kr.dreamstory.library.data.PlayerDataManger
import java.util.UUID

object PermissionManager {
    fun setGrade(uuid: UUID, grade: PermissionGrade) {
        val data = PlayerDataManger.getPlayerData(uuid) ?: return
        data.setPermission(grade)
    }
}