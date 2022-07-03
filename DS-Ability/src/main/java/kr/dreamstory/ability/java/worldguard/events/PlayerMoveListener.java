package kr.dreamstory.ability.java.worldguard.events;

import kr.dreamstory.ability.api.DSCoreAPI;
import com.dreamstory.ability.core.sub.ChannelCore;
import com.dreamstory.ability.java.worldguard.WorldGuardSupport;
import com.dreamstory.ability.listener.interfaces.ChannelListener;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import kr.dreamstory.ability.ability.VariableKt;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;

public class PlayerMoveListener implements ChannelListener {

    private DSCoreAPI api;
    private ChannelCore main;
    private WorldGuard wg;
    private WorldGuardSupport wgUtils;
    private PluginManager pluginManager;

    public PlayerMoveListener() {
        this.main = (ChannelCore) VariableKt.getSubCore();
        api = DSCoreAPI.INSTANCE;
        wgUtils = main.getWorldGuardSupport();
        wg = wgUtils.getWorldGuard();
        pluginManager = main.getServer().getPluginManager();
    }

    private void move(PlayerMoveEvent e, Reason reason) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            ProtectedRegion fromRegion = wgUtils.getProtectedRegion(from);
            ProtectedRegion toRegion = wgUtils.getProtectedRegion(to);
            if (fromRegion != null && fromRegion != toRegion) {
                ProtectRegionLeaveEvent leaveEvent = new ProtectRegionLeaveEvent(player, fromRegion, toRegion, reason);
                pluginManager.callEvent((Event)leaveEvent);
                e.setCancelled(leaveEvent.isCancelled());
            }
            if (toRegion != null && toRegion != fromRegion) {
                ProtectRegionEnterEvent enterEvent = new ProtectRegionEnterEvent(player, toRegion, reason,to,from);
                pluginManager.callEvent((Event)enterEvent);
                e.setCancelled(enterEvent.isCancelled());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ProtectedRegion region = wgUtils.getProtectedRegion(player.getLocation());
        if (region != null) {
            ProtectRegionLeaveEvent event = new ProtectRegionLeaveEvent(player, region,null, Reason.QUIT);
            pluginManager.callEvent((Event)event);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final ProtectedRegion region = wgUtils.getProtectedRegion(player.getLocation());
        if (region != null) {
            ProtectRegionEnterEvent event = new ProtectRegionEnterEvent(player, region, Reason.JOIN,null,null);
            pluginManager.callEvent((Event)event);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        final Player player = e.getEntity();
        final ProtectedRegion region = wgUtils.getProtectedRegion(player.getLocation());
        if (region != null) {
            ProtectRegionLeaveEvent event = new ProtectRegionLeaveEvent(player, region, null, Reason.DEATH);
            pluginManager.callEvent((Event)event);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        final ProtectedRegion region = wgUtils.getProtectedRegion(e.getRespawnLocation());
        if (region != null) {
            ProtectRegionEnterEvent event = new ProtectRegionEnterEvent(player, region, Reason.RESPAWN,null,null);
            pluginManager.callEvent((Event)event);
        }
    }

    /*@EventHandler
    public void onBroomMove(BroomMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            ProtectedRegion fromRegion = wgUtils.getProtectedRegion(from);
            ProtectedRegion toRegion = wgUtils.getProtectedRegion(to);
            if (fromRegion != null && fromRegion != toRegion) {
                ProtectRegionLeaveEvent leaveEvent = new ProtectRegionLeaveEvent(player, fromRegion, toRegion, Reason.MOVE);
                pluginManager.callEvent((Event)leaveEvent);
            }
            if (toRegion != null && toRegion != fromRegion) {
                ProtectRegionEnterEvent enterEvent = new ProtectRegionEnterEvent(player, toRegion, Reason.MOVE,to,from);
                pluginManager.callEvent((Event)enterEvent);
            }
        }
    }*/

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        move(e, Reason.MOVE);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        move((PlayerMoveEvent)e, Reason.TELEPORT);
    }
}
