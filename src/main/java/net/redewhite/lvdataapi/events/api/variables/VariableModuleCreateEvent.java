package net.redewhite.lvdataapi.events.api.variables;

import net.redewhite.lvdataapi.modules.VariableCreationModule;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class VariableModuleCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final VariableCreationModule variableCreationModule;
    private boolean cancel = false;

    public VariableModuleCreateEvent(VariableCreationModule variableCreationModule) {
        this.variableCreationModule = variableCreationModule;
    }

    public VariableCreationModule getVariableCreationModule() {
        return variableCreationModule;
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
