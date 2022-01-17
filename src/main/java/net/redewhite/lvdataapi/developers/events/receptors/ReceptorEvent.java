package net.redewhite.lvdataapi.developers.events.receptors;

import net.redewhite.lvdataapi.modules.ReceptorCreator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReceptorEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final ReceptorCreator receptor;

    public ReceptorEvent(boolean isAsync, ReceptorCreator receptor) {
        super(isAsync);
        this.receptor = receptor;
    }

    public ReceptorCreator getReceptor() {
        return receptor;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
}
