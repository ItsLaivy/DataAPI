package net.redewhite.lvdataapi.events.api.receptors;

import net.redewhite.lvdataapi.events.api.VariableReceptorEvent;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.creators.VariablesTable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class VariableReceptorUnloadEvent extends VariableReceptorEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;

    public VariableReceptorUnloadEvent(VariableReceptor receptor, VariablesTable table) {
        super(receptor, table);
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
