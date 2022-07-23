package kr.dreamstory.community.extension

import kr.dreamstory.community.chat.CommunityManager
import kr.dreamstory.community.chat.CommunityData
import kr.dreamstory.library.data.PlayerData

val PlayerData.communityData: CommunityData?
    get() = CommunityManager.getCommunityData(uuid)