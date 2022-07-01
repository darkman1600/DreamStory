package kr.dreamstory.library

import kr.dreamstory.library.message.MessageManager

object DSLibraryAPI {
    fun getDataFolder() = main.dataFolder
    val messageManager = MessageManager
}