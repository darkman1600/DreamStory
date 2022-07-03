package com.dreamstory.ability.mysql

data class MysqlUser(
    val host: String?,
    val port: String?,
    val database: String?,
    val user: String?,
    val password: String?
) {
    fun isEmpty(): Boolean = host == null || port == null || database == null || user == null || password == null
}
