package kr.dreamstory.ability.ability.play.block

import kr.dreamstory.ability.ability.event.AbilityBlockBreakEvent
import kr.dreamstory.ability.ability.event.AbilityObjectInteractEvent
import kr.dreamstory.ability.ability.main
import kr.dreamstory.ability.ability.play.ability.Ability
import kr.dreamstory.ability.ability.play.ability.AbilityType
import kr.dreamstory.ability.ability.play.block.obj.BreakAbleBlock
import kr.dreamstory.ability.extension.ability
import com.dreamstory.ability.extension.naturalDrop
import kr.dreamstory.ability.util.doubleFormat
import kr.dreamstory.library.coroutine.SynchronizationContext
import kr.dreamstory.library.coroutine.schedule
import net.minecraft.server.level.EntityPlayer
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
import org.bukkit.potion.PotionEffectType
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class FarmObject(
    type: AbilityType,
    key: String,
    regionName: String,
    prevLevel: Int,
    maxLevel: Double,
    exp: Long,
    drop: ItemStack?,
    val data: String?
): AbilityObject(
    type,
    key,
    regionName,
    prevLevel,
    maxLevel,
    exp,
    drop?: ItemStack(Material.STONE),
    data
), BreakAbleBlock {

    var minTime: Double
    var time: Double
    var sound: Sound
    var volume: Float
    var pitch: Float
    var emeraldPercent: Double = 0.005

    init {
        minTime = 1.0
        time  = 5.0
        sound  = Sound.BLOCK_STONE_BREAK
        volume  = 1F
        pitch  = 1F
        if(data != null) load()
    }


    override fun load() {
        val args = data!!.split(" : ")
        minTime = args[0].toDouble()
        time = args[1].toDouble()
        sound = Sound.valueOf(args[2])
        volume = args[3].toFloat()
        pitch = args[4].toFloat()
    }

    override fun startBreak(player: Player, tool: ItemStack, block: Block) {
        val uuid = player.uniqueId
        val ab = player.ability ?: return
        if(BreakAbleBlock.breakPlayers.contains(uuid)) return

        if(tool.type != Material.NETHERITE_HOE) return

        val event = AbilityObjectInteractEvent(player, this, ab, AbilityType.FARM, block)
        pluginManager.callEvent(event)
        if(event.isCancelled) return
        BreakAbleBlock.breakPlayers.add(uuid)

        val toolAttack = BigDecimal(100).divide(BigDecimal(100), 2 , RoundingMode.DOWN)
        val time = BigDecimal(time).divide(toolAttack.multiply(event.plusBigDecimal), 2, RoundingMode.DOWN).toDouble()

        val bossBar = server.createBossBar("\u99a1\uF82B\uF809\uF808§f채집 남은 시간 : §a${time.doubleFormat()}§f\uF82A\uF828\u99a2", BarColor.GREEN, BarStyle.SOLID)
        bossBar.progress = 1.0
        bossBar.isVisible = true
        bossBar.addPlayer(player)

        player.world.playSound(block.location, sound, volume, pitch)

        server.scheduler.schedule(main, SynchronizationContext.ASYNC) {
            var soundTick = System.currentTimeMillis()
            var handAnimationTick = System.currentTimeMillis()
            val slowAni = 300 / event.breakPower
            var current = time
            val entityPlayer = (player as CraftPlayer).handle
            while(true) {
                if(!player.isOnline || player.inventory.itemInMainHand != tool) {
                    cancelBreak(block, entityPlayer, bossBar, uuid)
                    break
                }
                val now = System.currentTimeMillis()
                val targetBlock = player.getTargetBlock(5)
                if(targetBlock == null || targetBlock != block) {
                    cancelBreak(block, entityPlayer, bossBar, uuid)
                    break
                }

                var damage = (9.0-(9.0 * (current / time))).toInt()
                if(damage < 0) damage = 0
                else if(damage > 9) damage = 9
                BreakAbleBlock.actionBlockBreakAnimation(block, entityPlayer, damage)

                if(current <= 0) {
                    switchContext(SynchronizationContext.SYNC)
                    breakBlock(player, block, ab, false)
                    cancelBreak(block, entityPlayer, bossBar, uuid)
                    player.world.playSound(block.location, Sound.BLOCK_GRASS_BREAK, 1F, 1F)
                    break
                }

                val big1 = BigDecimal(now)
                val big2 = BigDecimal(soundTick)
                soundTick = now
                val n = BigDecimal(current)
                current = n.subtract(big1.subtract(big2).divide(BigDecimal(1000), 2, RoundingMode.HALF_UP)).toDouble()
                if(current < 0) current = 0.0
                var progress = current / time
                if(progress < 0) progress = 0.0
                bossBar.progress = progress
                bossBar.setTitle("\u99a1\uF82B\uF809\uF808§f채집 남은 시간 : §a${current.doubleFormat()}§f\uF82A\uF828\u99a2")

                if(handAnimationTick + 300 <= now) {
                    player.swingMainHand()
                    handAnimationTick = now
                    player.world.playSound(block.location, sound, volume, pitch)
                    block.world.spawnParticle(Particle.BLOCK_CRACK, block.location.add(.5, .5, .5), 30, .3, .3, .3, block.blockData)
                    if(player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) block.world.spawnParticle(Particle.FALLING_WATER, block.location.add(.5, 1.5, .5), 5, .3, .1, .3)
                }
                waitFor(1)
            }
        }
    }

    private fun cancelBreak(b: Block, entityPlayer: EntityPlayer, bossBar: BossBar, uuid: UUID) {
        BreakAbleBlock.actionBlockBreakAnimation(b, entityPlayer, 10)
        BreakAbleBlock.breakPlayers.remove(uuid)
        bossBar.removeAll()
    }

    override fun breakBlock(player: Player, block: Block, ability: Ability, isSkill: Boolean) {
        val abEvent = AbilityBlockBreakEvent(player, this, ability, AbilityType.FARM, block, isSkill)
        pluginManager.callEvent(abEvent)
        if(abEvent.isCancelled) return
        block.world.spawnParticle(Particle.EXPLOSION_NORMAL, block.location.add(.5, .5, .5), 0)
        server.scheduler.schedule(main) {
            dropItem.naturalDrop(player, abEvent.dropPercent)
            ability.addExp(exp, type)
            BreakAbleBlock.startCoolTime(block, block.blockData, 1200)
            block.type = Material.AIR
        }
    }

    override fun toDataString(): String = "$minTime : $time : $sound : $volume : $pitch"
}