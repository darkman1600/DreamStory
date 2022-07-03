package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import com.dreamstory.ability.mysql.MysqlUser
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MysqlManager {

    var connection: Connection? = null
        private set
    private val user by lazy {
        val config = main.config
        MysqlUser(
            config.getString("mysql.address"),
            config.getString("mysql.port"),
            config.getString("mysql.database"),
            config.getString("mysql.host"),
            config.getString("mysql.password")
        )
    }

    fun setMysql(): Boolean {
        connection = getNewConnection() ?: return false
        if(!createTable()) return false
        main.server.scheduler.runTaskTimerAsynchronously(main, Runnable {
            try {
                if(connection?.isClosed == false) connection!!.createStatement().execute("SELECT 1")
                else connection = getNewConnection()
            } catch (e: SQLException) {
                connection = getNewConnection()
                e.printStackTrace()
            }
        },1200,1200)
        return true
    }

    inline fun <reified T> executeQuerySet(table: String, select: String, where: String, key: Any,option: String = ""): HashSet<T> {
        val s = connection!!.prepareStatement("SELECT $select FROM $table WHERE $where='$key'${if(option == "") "" else " $option"}")
        val set = s.executeQuery()
        val result: HashSet<T> = HashSet()
        var i = 1
        while(set.next()) { result.add(set.getObject(i++, T::class.java)) }
        set.close()
        s.close()
        return result
    }

    inline fun <reified T> executeQuery(table: String, select: String, where: String, key: Any,option: String=""): T? {
        val s = connection!!.prepareStatement("SELECT $select FROM $table WHERE $where='$key'${if(option == "") "" else " $option"}")
        val set = s.executeQuery()
        var result: T? = null
        if(set.next()) {
            result = set.getObject(1, T::class.java)
        }
        set.close()
        s.close()
        return result
    }

    fun executeQuery(table: String, where: String, key: Any): HashMap<String, Any> {
        val s = connection!!.prepareStatement("SELECT * FROM $table WHERE $where=$key")
        val set = s.executeQuery()
        val result = HashMap<String,Any>()
        if(set.next()) {
            val meta = set.metaData
            for(i in 1 .. meta.columnCount) result[meta.getColumnName(i)] = set.getObject(i)
        }
        set.close()
        s.close()
        return result
    }

    fun executeQuery(sql: String) {
        val s = connection!!.prepareStatement(sql)
        s.execute()
        s.close()
    }

    private fun getNewConnection(): Connection? = if(user.isEmpty()) { main.logger.info("§c데이터 베이스에 접근할 수 없었습니다.");null } else {
        try {
                Class.forName("com.mysql.jdbc.Driver")
                val url = "jdbc:mysql://${user.host}:${user.port}/${user.database}"
                DriverManager.getConnection(url, user.user, user.password)
        } catch (e: Exception) { null }
    }

    private fun createTable(): Boolean {
        return try {
            if(connection?.isClosed != false) {
                connection = getNewConnection()
                if(connection?.isClosed != false) {
                    main.logger.info("§c데이터 베이스에 접근할 수 없었습니다.")
                    return false
                }
            }

            val state = connection!!.createStatement()

            state.execute("CREATE TABLE IF NOT EXISTS server ("
                    + "port INT NOT NULL primary key,"
                    + "name TEXT NOT NULL,"
                    + "type TEXT NOT NULL,"
                    + "players TEXT NOT NULL,"
                    + "status TEXT NOT NULL"
                    + ")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS island ("
                    +"id INT NOT NULL auto_increment primary key,"
                    +"uuid TEXT NOT NULL,"
                    +"owner INT NOT NULL,"
                    +"sub_owners TEXT NOT NULL,"
                    +"member_size INT NOT NULL,"
                    +"icon TEXT NOT NULL,"
                    +"exp LONG NOT NULL,"
                    +"point LONG NOT NULL,"
                    +"name TEXT NOT NULL,"
                    +"spawn TEXT NOT NULL,"
                    +"warp TEXT NOT NULL,"
                    +"challenge INT NOT NULL,"
                    +"owner_name TEXT NOT NULL,"
                    +"sub_owner_name TEXT NOT NULL,"
                    +"member_name TEXT NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS island_option ("
                    +"island INT NOT NULL primary key,"
                    +"option_data LONGTEXT NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS island_board ("
                    + "id INT NOT NULL auto_increment primary key,"
                    + "island INT NOT NULL,"
                    + "title TEXT NOT NULL,"
                    + "text LONGTEXT NOT NULL,"
                    + "author INT NOT NULL,"
                    + "date LONG NOT NULL,"
                    + "priority INT NOT NULL"
                    + ")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS island_coop ("
                    +"player INT NOT NULL primary key,"
                    +"island INT NOT NULL,"
                    +"date LONG NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS ability ("
                    +"player INT NOT NULL primary key,"
                    +"farm_lv INT NOT NULL,"
                    +"farm_exp LONG NOT NULL,"
                    +"farm_skill LONGTEXT NOT NULL,"
                    +"mine_lv INT NOT NULL,"
                    +"mine_exp LONG NOT NULL,"
                    +"mine_skill LONGTEXT NOT NULL,"
                    +"hunt_lv INT NOT NULL,"
                    +"hunt_exp LONG NOT NULL,"
                    +"hunt_skill LONGTEXT NOT NULL,"
                    +"fish_lv INT NOT NULL,"
                    +"fish_exp LONG NOT NULL,"
                    +"fish_skill LONGTEXT NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS cool_times ("
                    +"player INT NOT NULL primary key,"
                    +"cool_times LONGTEXT NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS player ("
                    +"id INT NOT NULL auto_increment primary key,"
                    +"uuid TEXT NOT NULL,"
                    +"name TEXT NOT NULL," // nick Name
                    +"real_name TEXT NOT NULL," // real Name
                    +"money BIGINT NOT NULL DEFAULT 0,"
                    +"cash INT NOT NULL DEFAULT 0,"
                    +"inv TEXT NOT NULL,"
                    +"last_location TEXT NOT NULL,"
                    +"dest_location VARCHAR(100) NOT NULL DEFAULT 'none',"
                    +"last_connect LONG NOT NULL,"
                    +"island INT NOT NULL DEFAULT 0,"
                    +"op BOOL NOT NULL,"
                    +"hp DOUBLE NOT NULL,"
                    +"maxHp DOUBLE NOT NULL,"
                    +"hungry INT NOT NULL,"
                    +"ban BOOL NOT NULL,"
                    +"head_item TEXT NOT NULL,"
                    +"dungeon INT NOT NULL DEFAULT 3,"
                    +"region INT NOT NULL DEFAULT 0,"
                    +"port INT NOT NULL DEFAULT 0"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS player_option ("
                    +"player INT NOT NULL primary key,"
                    +"option_data LONGTEXT NOT NULL,"
                    +"chat_mode VARCHAR(10) NOT NULL DEFAULT 'ALL'"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS postbox ("
                    +"id INT NOT NULL auto_increment primary key,"
                    +"player INT NOT NULL,"
                    +"sender INT NOT NULL,"
                    +"type INT NOT NULL,"
                    +"date LONG NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS giftbox ("
                    +"id INT NOT NULL auto_increment primary key,"
                    +"player INT NOT NULL,"
                    +"sender INT NOT NULL,"
                    +"contents TEXT NOT NULL,"
                    +"date LONG NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS region ("
                    +"id INT NOT NULL auto_increment primary key,"
                    +"wg_name TEXT NOT NULL,"
                    +"name TEXT NOT NULL,"
                    +"des VARCHAR(100) NOT NULL DEFAULT '',"
                    +"spawn TEXT NOT NULL,"
                    +"weather INT NOT NULL DEFAULT 0,"
                    +"type INT NOT NULL DEFAULT 0,"
                    +"last_update LONG NOT NULL"
                    +")"
            )

            state.execute("CREATE TABLE IF NOT EXISTS friend ("
                    +"player INT NOT NULL primary key,"
                    +"friends LONGTEXT NOT NULL"
                    +")"
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}