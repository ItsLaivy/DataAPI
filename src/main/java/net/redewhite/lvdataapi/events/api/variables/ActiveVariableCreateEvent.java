package net.redewhite.lvdataapi.events.api.variables;

import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class ActiveVariableCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ActiveVariableLoader activeVariable;
    private final VariableReceptor receptor;
    private boolean cancel = false;

    public ActiveVariableCreateEvent(ActiveVariableLoader activeVariable, VariableReceptor receptor) {
        this.activeVariable = activeVariable;
        this.receptor = receptor;
    }

    public ActiveVariableLoader getInactiveVariable() {
        return activeVariable;
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
