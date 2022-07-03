package kr.dreamstory.ability.java.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public final class ProtectRegionEnterEvent extends ProtectRegionEvent implements Cancellable {
    private static HandlerList HANDLERS;
    private Player player;
    private Reason reason;
    private boolean cancellable;
    private boolean cancelled;
    private Location to;
    private Location from;

    public ProtectRegionEnterEvent(@Nonnull Player player, @Nonnull ProtectedRegion region, @Nonnull Reason reason, Location to, Location from) {
        super(region);
        this.player = player;
        this.reason = reason;
        this.cancellable = (reason != Reason.QUIT && reason != Reason.JOIN && reason != Reason.RESPAWN);
        this.to = to;
        this.from = from;
    }

    public static HandlerList getHandlerList() {
        return ProtectRegionEnterEvent.HANDLERS;
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    public Location getTo() { return to; }

    public Location getFrom() { return from; }

    @Nonnull
    public Reason getReason() {
        return this.reason;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancellable() {
        return this.cancellable;
    }

    public HandlerList getHandlers() {
        return ProtectRegionEnterEvent.HANDLERS;
    }

    static {
        HANDLERS = new HandlerList();
    }
}
