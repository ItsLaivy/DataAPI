package codes.laivy.dataapi.events.receptors;

import codes.laivy.dataapi.modules.Receptor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReceptorEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Receptor receptor;

    public ReceptorEvent(boolean isAsync, Receptor receptor) {
        super(isAsync);
        this.receptor = receptor;
    }

    public Receptor getReceptor() {
        return receptor;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
}
