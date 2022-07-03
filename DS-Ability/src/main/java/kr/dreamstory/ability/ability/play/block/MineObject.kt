package kr.dreamstory.ability.ability.play.block

import kr.dreamstory.ability.ability.event.AbilityBlockBreakEvent
import kr.dreamstory.ability.ability.event.AbilityObjectInteractEvent
import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import kr.dreamstory.ability.ability.play.damageskin.DamageSkinManager
import com.dreamstory.ability.extension.ability
import com.dreamstory.ability.extension.getDoubleNbt
import com.dreamstory.ability.extension.getIntNbt
import com.dreamstory.ability.extension.naturalDrop
import com.dreamstory.ability.util.doubleFormat
import kr.dreamstory.library.coroutine.CoroutineTask
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import net.minecraft.server.level.EntityPlayer
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.roundToInt

class MineObject(
    type: AbilityType,
    key: String,
    regionId: Int,
    prevLevel: Int,
    maxLevel: Double,
    exp: Long,
    drop: ItemStack?,
    val data: String?
): AbilityObject(
    type,
    key,
    regionId,
    prevLevel,
    maxLevel,
    exp,
    drop?: ItemStack(Material.STONE),
    data
), BreakAbleBlock {

    var sound: Sound
    var volume: Float
    var pitch: Float
    var maxHealth: Double
    var emeraldPercent: Double = 0.005

    val optionMap = HashMap<Block, BlockOptions>()
    val prevBlock = HashMap<UUID,Block>()
    val idList = HashSet<Int>()

    init {
        maxHealth = 100.0
        sound = Sound.BLOCK_STONE_BREAK
        volume = 1F
        pitch = 1F
        if(data != null) load()
    }

    private fun getNewPrevBlock(uuid: UUID, block: Block): Block {
        prevBlock[uuid] = block
        return prevBlock[uuid]!!
    }

    override fun load() {
        val args = data!!.split(" : ")
        maxHealth = args[0].toDouble()
        sound = Sound.valueOf(args[1])
        volume = args[2].toFloat()
        pitch = args[3].toFloat()
    }

    override fun toDataString(): String = "$maxHealth : $sound : $volume : $pitch"

    inner class BlockOptions(val owner: UUID, val block: Block) {
        var isBreak: Boolean = false
        var hp: Double = maxHealth
        var bossBar: BossBar? = null
        var task: CoroutineTask? = null
        var timerCount: Int = 0
        //var hitBoxId: Int? = null

        fun clear(entityPlayer: EntityPlayer, b: Block) {
            isBreak = true
            bossBar?.removeAll()
            task?.cancel()
            BreakAbleBlock.actionBlockBreakAnimation(b,entityPlayer,10)
            //removeHitBox(entityPlayer,hitBoxId!!)
            removeBlockOptions(b)
            removePrevBlock(owner)
        }
    }
    private fun removeBlockOptions(block: Block) = optionMap.remove(block)
    private fun removePrevBlock(uuid: UUID) = prevBlock.remove(uuid)
    private fun getNewOptions(uuid: UUID, block: Block): BlockOptions {
        optionMap[block] = BlockOptions(uuid,block)
        return optionMap[block]!!
    }

    override fun startBreak(player: Player, tool: ItemStack, block: Block) {
        val uuid = player.uniqueId

        if(tool.type != Material.NETHERITE_PICKAXE) return

        val ab = player.ability ?: return
        val event = AbilityObjectInteractEvent(player, this, ab, AbilityType.MINE, block)
        pluginManager.callEvent(event)
        if(event.isCancelled) return

        val entityPlayer = (player as CraftPlayer).handle

        var options = optionMap[block] // ?: getNewOptions(uuid,block)
        if(options != null) {
            if (options.owner != uuid) {
                player.sendMessage("다른 사람이 캐는 중")
                return
            }
        } else {
            options = getNewOptions(uuid,block)
        }

        if(player.hasCooldown(tool.type)) return
        val toolSpeed = tool.getIntNbt("toolSpeed") ?: 16
        player.setCooldown(tool.type,toolSpeed)

        val previous = prevBlock[uuid] ?: getNewPrevBlock(uuid,block)
        val prevOptions = optionMap[previous]
        if(previous != block && prevOptions != null) {
            prevOptions.clear(entityPlayer,previous)
            getNewPrevBlock(uuid,block)
        }
        val toolDamage = tool.getDoubleNbt("tool_damage") ?: 1.0
        val critical = (1..1000).random() <= (tool.getIntNbt("tool_critical") ?: 100)
        val damage = if(critical) toolDamage * 2 else toolDamage

        main.server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            if(options.hp - damage <= .0) {
                switchContext(SynchronizationContext.SYNC)
                breakBlock(player,block,ab,false)
                options.clear(entityPlayer,block)
            } else {
                options.hp -= damage
            }

            val percent = options.hp / maxHealth
            val title = "\u99a1\uF82B\uF809\uF808§f채굴 남은 체력 : §a${options.hp.doubleFormat()}§f\uF82A\uF828\u99a2"
            if(options.bossBar == null) {
                val newBar = server.createBossBar(title, BarColor.WHITE, BarStyle.SOLID)
                newBar.isVisible = true
                newBar.progress = percent
                newBar.addPlayer(player)
                options.bossBar = newBar
            } else {
                val prevBar = options.bossBar!!
                prevBar.setTitle(title)
                prevBar.progress = percent
            }

            //if(options.hitBoxId == null) options.hitBoxId = createHitBox(player.world,entityPlayer,block)
            DamageSkinManager.show(damage.doubleFormat(),block.location.add(.5,0.8,.5))
            BreakAbleBlock.actionBlockBreakAnimation(block,entityPlayer,9 - (percent * 9).roundToInt())
            block.world.spawnParticle(Particle.BLOCK_DUST, block.location.add(.5, .5, .5), 30, .3, .3, .3, block.blockData)
            player.world.playSound(block.location, sound, volume, pitch)
            if(critical) {
                player.world.playSound(block.location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.08F, 0.6F)
                block.world.spawnParticle(Particle.CRIT, block.location.add(.5, .5, .5), 30,.0,.0,.0,.5)
            }
            startTimer(options,entityPlayer)
        }
    }

    private fun startTimer(options: BlockOptions, entityPlayer: EntityPlayer) {
        options.timerCount = 0
        if(options.task != null) return
        options.task = server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            while(options.timerCount < 15) {
                waitFor(20)
                options.timerCount ++
            }
            if(!options.isBreak) {
                options.clear(entityPlayer,options.block)
            }
        }
    }
    override fun breakBlock(player: Player, block: Block, ability: Ability, isSkill: Boolean) {
        val abEvent = AbilityBlockBreakEvent(player, this, ability, AbilityType.MINE, block, isSkill)
        pluginManager.callEvent(abEvent)
        if(abEvent.isCancelled) return
        block.world.spawnParticle(Particle.REDSTONE, block.location.add(.5, .5, .5), 10, .3, .3, .3,Particle.DustOptions(Color.WHITE,2.5F))
        dropItem.naturalDrop(player, abEvent.dropPercent)
        ability.addExp(exp, type)
        BreakAbleBlock.startCoolTime(block, block.blockData, 1200)
        block.type = Material.AIR
        player.world.playSound(block.location,Sound.BLOCK_GLASS_BREAK,1F,2F)
    }

    /*override fun createHitBox(world: World, entityPlayer: EntityPlayer, block: Block): Int {
        val connection = entityPlayer.b
        val craftWorld = (world as CraftWorld).handle
        val bLoc = block.location.add(.5,100.0,.5)

        val blockData = CraftMagicNumbers.getBlock(block.type).n()
        val hitBox = EntityFallingBlock(craftWorld,bLoc.x,bLoc.y,bLoc.z,blockData)

        val bukkitEntity = hitBox.bukkitEntity
        bukkitEntity.isGlowing = true
        bukkitEntity.isInvulnerable = true
        bukkitEntity.setGravity(false)

        val spawnPacket = PacketPlayOutSpawnEntity(hitBox,net.minecraft.world.level.block.Block.i(blockData))
        val metadata = PacketPlayOutEntityMetadata(hitBox.bukkitEntity.entityId,hitBox.ai(),false)

        hitBox.bukkitEntity.teleport(block.location.add(.5,.0,.5))
        val packetTeleport = PacketPlayOutEntityTeleport(hitBox)

        connection.a(spawnPacket)
        connection.a(metadata)
        connection.a(packetTeleport)

        return hitBox.b
    }

    override fun removeHitBox(entityPlayer: EntityPlayer, hitBoxId: Int) {
        val connection = entityPlayer.b
        val destroyPacket = PacketPlayOutEntityDestroy(hitBoxId)
        connection.a(destroyPacket)
    }*/
}