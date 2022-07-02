package kr.dreamstory.ability.ability

import com.dreamstory.ability.core.GUI
import kr.dreamstory.ability.core.main.DSAbility
import com.dreamstory.ability.core.sub.Core
import java.util.*

internal lateinit var main: DSAbility
internal lateinit var subCore: Core

private var update = false


val guiMap = HashMap<UUID, GUI>()

