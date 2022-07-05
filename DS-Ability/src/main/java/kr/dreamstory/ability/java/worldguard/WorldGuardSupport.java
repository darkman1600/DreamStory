package kr.dreamstory.ability.java.worldguard;

import kr.dreamstory.ability.core.sub.ChannelCore;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

public class WorldGuardSupport {

    private static WorldGuardSupport instance;
    private ChannelCore main;
    private WorldGuard wg;
    private WorldGuardSupport(ChannelCore main) { instance = this; this.main = main; wg = WorldGuard.getInstance(); }
    public static WorldGuardSupport getInstance(ChannelCore main) { return instance == null ? new WorldGuardSupport(main) : instance; }

    @Nullable
    public ProtectedRegion getProtectedRegion(@Nonnull final Location location) {
        final RegionContainer container = wg.getPlatform().getRegionContainer();
        final RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        ProtectedRegion region = null;
        if (manager != null) {
            final ApplicableRegionSet fromRegions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
            region = (ProtectedRegion)fromRegions.getRegions().stream().max(Comparator.comparing(ProtectedRegion::getPriority)).orElse(null);
        }
        return region;
    }

    public WorldGuard getWorldGuard() { return wg; }
}
