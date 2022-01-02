package net.redewhite.lvdataapi.developers.events.variables;

import net.redewhite.lvdataapi.receptors.ActiveVariable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActiveVariableValueChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ActiveVariable variable;
    private final Object newValue;
    private boolean cancelled = false;

    public ActiveVariableValueChangeEvent(ActiveVariable variable, Object newValue) {
        this.variable = variable;
        this.newValue = newValue;
    }

    public ActiveVariable getVariable() {
        return variable;
    }

    public Object getNewValue() {
        return newValue;
    }

    public static HandlerList getHanderList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
