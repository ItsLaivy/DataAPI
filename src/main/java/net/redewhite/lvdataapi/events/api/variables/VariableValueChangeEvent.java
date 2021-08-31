package net.redewhite.lvdataapi.events.api.variables;

import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.events.api.VariableEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class VariableValueChangeEvent extends VariableEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected Boolean cancel = false;
    protected Object newValue;
    protected Object value;

    public VariableValueChangeEvent(ActiveVariableLoader variable, VariableReceptor receptor, Object newValue, Object value) {
        super(variable, receptor);
        this.newValue = newValue;
        this.value = value;
    }

    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        cancel = cancelled;
    }

    public Object getNewValue() {
        return newValue;
    }
    public Object getValue() {
        return value;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
