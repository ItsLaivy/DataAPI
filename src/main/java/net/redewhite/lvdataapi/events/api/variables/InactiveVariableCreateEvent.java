package net.redewhite.lvdataapi.events.api.variables;

import net.redewhite.lvdataapi.loaders.InactiveVariableLoader;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;


@SuppressWarnings("unused")
public class InactiveVariableCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final InactiveVariableLoader inactiveVariable;
    private final VariableReceptor receptor;
    private boolean cancel = false;

    public InactiveVariableCreateEvent(InactiveVariableLoader inactiveVariableLoader, VariableReceptor receptor) {
        this.inactiveVariable = inactiveVariableLoader;
        this.receptor = receptor;
    }

    public InactiveVariableLoader getInactiveVariable() {
        return inactiveVariable;
    }
    public VariableReceptor getReceptor() {
        return receptor;
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
