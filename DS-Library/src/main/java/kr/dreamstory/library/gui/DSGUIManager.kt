package com.dreamstory.library.gui

import kr.dreamstory.library.gui.DSGUI
import java.util.*

object DSGUIManager {

    private val playerGUI = HashMap<UUID, DSGUI>()

    fun inputPlayerGUI(uuid: UUID, gui: DSGUI) { playerGUI[uuid] = gui }
    fun removePlayerGUI(uuid: UUID) { playerGUI.remove(uuid) }

    fun getPlayerGUI(uuid: UUID) = playerGUI[uuid]

}