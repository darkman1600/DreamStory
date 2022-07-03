package kr.dreamstory.library.gui

import java.util.*

object DSGUIManager {

    private val playerGUI = HashMap<UUID, DSGUI>()

    fun inputPlayerGUI(uuid: UUID, gui: DSGUI) { playerGUI[uuid] = gui }
    fun removePlayerGUI(uuid: UUID) { playerGUI.remove(uuid) }

    fun getPlayerGUI(uuid: UUID) = playerGUI[uuid]

}