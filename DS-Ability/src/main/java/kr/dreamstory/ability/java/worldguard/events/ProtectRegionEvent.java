package kr.dreamstory.ability.java.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class ProtectRegionEvent extends Event {
    private final ProtectedRegion region;

    ProtectRegionEvent(@Nonnull final ProtectedRegion region) {
        this.region = region;
    }

    @Nonnull
    public ProtectedRegion getRegion() {
        return this.region;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
