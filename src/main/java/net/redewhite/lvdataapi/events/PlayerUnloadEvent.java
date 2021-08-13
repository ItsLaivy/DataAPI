package net.redewhite.lvdataapi.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnloadEvent extends Event {

    private final Player player;
    private Boolean success;
    private static final HandlerList handlers = new HandlerList();

    public PlayerUnloadEvent(Player player) {
        this.player = player;
        this.success = true;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean getSuccess() {
        return success;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setSuccess(Boolean state) {
        this.success = state;
    }
}
