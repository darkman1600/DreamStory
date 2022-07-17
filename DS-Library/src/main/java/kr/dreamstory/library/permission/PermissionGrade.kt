package kr.dreamstory.library.permission

import kr.dreamstory.library.item.dreamstory.item.enums.DSItemType

enum class PermissionGrade(
    val display: String,
    val color: String,
    val quantity: Int,
    val font: String
) {
    NEWBIE("초보자","§7",0,"\u01a0"),
    USER("유저","§f",1,"\u01a1"),
    GUIDE("가이드","§e",2,"\u01a2"),
    STAFF("스탭","§d",3,"\u01a3"),
    DEVELOPER("개발자","§6",4,"\u01a4"),
    ADMIN("관리자","§c",5,"\u01a5");

    companion object {
        fun fromString(type: String) = typeMap[type]
        private val typeMap = HashMap<String, PermissionGrade>()
        init {
            values().forEach {
                typeMap[it.name] = it
            }
        }
    }
}