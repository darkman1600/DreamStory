package kr.dreamstory.ability.java.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProtectRegionLeaveEvent extends ProtectRegionEvent implements Cancellable {
    private static HandlerList HANDLERS;
    private Player player;
    private Reason reason;
    private boolean cancellable;
    private boolean cancelled;
    private ProtectedRegion fromRegion;

    public ProtectRegionLeaveEvent(@Nonnull Player player, @Nonnull ProtectedRegion region, @Nullable ProtectedRegion fromRegion, @Nonnull Reason reason) {
        super(region);
        this.fromRegion = fromRegion;
        this.player = player;
        this.reason = reason;
        this.cancellable = (reason != Reason.QUIT && reason != Reason.JOIN && reason != Reason.RESPAWN);
    }

    public static HandlerList getHandlerList() {
        return ProtectRegionLeaveEvent.HANDLERS;
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    @Nonnull
    public Reason getReason() {
        return this.reason;
    }

    @Nullable
    public ProtectedRegion getFromRegion() { return fromRegion; }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancellable() {
        return this.cancellable;
    }

    public HandlerList getHandlers() {
        return ProtectRegionLeaveEvent.HANDLERS;
    }

    static {
        HANDLERS = new HandlerList();
    }
}
