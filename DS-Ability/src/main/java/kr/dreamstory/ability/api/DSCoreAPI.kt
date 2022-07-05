package kr.dreamstory.ability.api

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.util.SignMenuFactory

object DSCoreAPI {

    val signMenuFactory by lazy { SignMenuFactory(main) }

}