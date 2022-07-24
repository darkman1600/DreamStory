package kr.dreamstory.library

import kr.dreamstory.library.data.PlayerDataManger
import kr.dreamstory.library.item.dreamstory.data.DSItemManager
import kr.dreamstory.library.utils.message.MessageManager
import kr.dreamstory.library.utils.SignMenuFactory

object DSLibraryAPI {
    fun getInstance() = main

    fun getDataFolder() = main.dataFolder
    val messageManager = MessageManager
    val dsItemManager = DSItemManager
    val playerDataManager = PlayerDataManger

    val signMenuFactory by lazy { SignMenuFactory(main) }
}