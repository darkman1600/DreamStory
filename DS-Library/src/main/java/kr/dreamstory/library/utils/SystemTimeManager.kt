package kr.dreamstory.library.utils

import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import kr.dreamstory.library.main
import kr.dreamstory.library.systemTime


object SystemTimeManager {
    fun startTask() {
        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            while (main.isEnabled) {
                systemTime = System.currentTimeMillis()
                waitFor(1)
            }
        }
    }
}