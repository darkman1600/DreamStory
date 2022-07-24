package kr.dreamstory.library.utils.sound

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import org.bukkit.Sound
import org.bukkit.entity.Player

object SoundManager {

    fun turnOff(player: Player) {
        main.schedule(SynchronizationContext.ASYNC) {
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,1F,2F)
            waitFor(2)
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,1F,1.5F)
        }
    }

    fun turnOn(player: Player) {
        main.schedule(SynchronizationContext.ASYNC) {
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,1F,1.5F)
            waitFor(2)
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,1F,2F)
        }
    }
}