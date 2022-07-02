package com.dreamstory.ability.manager

import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.*
import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

object AbilityBlockManager {

    private val blockMap = HashMap<String, AbilityObject>()

    private val dataFolder = File(main.dataFolder, "blocks")
    private val dataFiles = arrayOf(
        File(dataFolder, "farm.data"), File(dataFolder, "mine.data"),
        File(dataFolder, "hunt.data"), File(dataFolder, "fish.data")
    )

    fun load() {
        dataLoad(AbilityType.FARM) { config, key ->
            val farmBlock = FarmObject(
            AbilityType.valueOf(config.getString("$key.type")!!),
            key,
            config.getInt("$key.region"),
            config.getInt("$key.minLevel"),
            config.getDouble("$key.maxLevel"),
            config.getLong("$key.exp"),
            ItemListManager.singleItemStackArrayFromBase64(config.getString("$key.drop")),
            config.getString("$key.data"))
            blockMap[key] = farmBlock
        }
        dataLoad(AbilityType.MINE) { config, key ->
            val mineBlock = MineObject(
                AbilityType.valueOf(config.getString("$key.type")!!),
                key,
                config.getInt("$key.region"),
                config.getInt("$key.minLevel"),
                config.getDouble("$key.maxLevel"),
                config.getLong("$key.exp"),
                ItemListManager.singleItemStackArrayFromBase64(config.getString("$key.drop")),
                config.getString("$key.data"))
            blockMap[key] = mineBlock
        }
        dataLoad(AbilityType.HUNT) { config, key ->
            val huntBlock = HuntObject(
                AbilityType.valueOf(config.getString("$key.type")!!),
                key,
                config.getInt("$key.region"),
                config.getInt("$key.minLevel"),
                config.getDouble("$key.maxLevel"),
                config.getLong("$key.exp"),
                ItemListManager.singleItemStackArrayFromBase64(config.getString("$key.drop")),
                config.getString("$key.data"))
            blockMap[key] = huntBlock
        }
        dataLoad(AbilityType.FISH) { config, key ->
            val fishBlock = FishObject(
                AbilityType.valueOf(config.getString("$key.type")!!),
                key,
                config.getInt("$key.region"),
                config.getInt("$key.minLevel"),
                config.getDouble("$key.maxLevel"),
                config.getLong("$key.exp"),
                ItemListManager.singleItemStackArrayFromBase64(config.getString("$key.drop")),
                config.getString("$key.data"))
            blockMap[key] = fishBlock
        }
    }

    fun save() {
        val list = Array<FileConfiguration?>(4) { null }
        AbilityType.values().forEach {
            if(it == AbilityType.NONE) return@forEach
            val config = YamlConfiguration.loadConfiguration(dataFiles[it.index])
            list[it.index] = config
        }

        blockMap.values.forEach { it.setConfig(list[it.type.index]!!) }
        try { list.forEachIndexed { i,it -> it!!.save(dataFiles[i]) }
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun getAbilityBlock(blockData: BlockData): AbilityObject? = getAbilityBlock(blockData.toString())
    fun getAbilityBlock(biome: Biome, regionId: Int): AbilityObject? = getAbilityBlock("$regionId : $biome")
    fun getAbilityBlock(key: String): AbilityObject? = blockMap[key]
    fun unregisterAbilityBlock(abilityObject: AbilityObject) { blockMap.remove(abilityObject.key) }
    fun action(abilityObject: AbilityObject, player: Player, o: Any, tool: ItemStack): Boolean {
        return when(abilityObject) {
            is BreakAbleBlock -> {
                abilityObject.startBreak(player,tool,o as Block)
                true
            }
            else -> false
        }
    }
    fun a(abilityObject: AbilityObject) {
        if(abilityObject is FarmObject) {
        }
    }

    fun registerAbilityBlock(biome: Biome, type: AbilityType, regionId: Int): Boolean {
        return registerAbilityBlock("$regionId : $biome", type, regionId)
    }
    fun registerAbilityBlock(key: String, type: AbilityType, regionId: Int): Boolean {
        if(blockMap.containsKey(key)) return false
        val ab: AbilityObject = when(type) {
            AbilityType.FARM ->
                FarmObject( type, key, regionId, 0,100.0,0, null, null)
            AbilityType.MINE ->
                MineObject( type, key, regionId, 0,100.0,0, null, null)
            AbilityType.FISH ->
                FishObject( type, key, regionId, 0, 100.0, 0, null, null)
            AbilityType.HUNT ->
                HuntObject( type, key ,regionId, 0 , 100.0, 0, null, null)
            else -> null
        }?: return false
        blockMap[key] = ab
        return true
    }

    private inline fun dataLoad(type: AbilityType, body:(FileConfiguration, String)->Unit) {
        val file = dataFiles[type.index]
        if(!file.exists()) return
        val config = YamlConfiguration.loadConfiguration(file)
        config.getKeys(false).forEach { body(config, it) }
    }


}