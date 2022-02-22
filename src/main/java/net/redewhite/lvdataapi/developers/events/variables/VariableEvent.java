package net.redewhite.lvdataapi.developers.events.variables;

import net.redewhite.lvdataapi.modules.VariableCreator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VariableEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final VariableCreator variable;

    public VariableEvent(boolean isAsync, VariableCreator variable) {
        super(isAsync);
        this.variable = variable;
    }

    public VariableCreator getVariable() {
        return variable;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

}
