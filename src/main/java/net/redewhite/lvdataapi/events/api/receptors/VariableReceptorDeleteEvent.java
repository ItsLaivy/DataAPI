package net.redewhite.lvdataapi.events.api.receptors;

import net.redewhite.lvdataapi.events.api.VariableReceptorEvent;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class VariableReceptorDeleteEvent extends VariableReceptorEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;

    public VariableReceptorDeleteEvent(VariableReceptor receptor) {
        super(receptor);
    }

    public boolean isCancelled() {
        return cancel;
    }
    public void setCancelled(boolean cancelled) {
        cancel = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
