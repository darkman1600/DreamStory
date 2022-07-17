package kr.dreamstory.community.request

import org.bukkit.entity.Player

class RequestFactory(

    val type: RequestType,
    val requester: Player,
    val receiver: Player

){

}