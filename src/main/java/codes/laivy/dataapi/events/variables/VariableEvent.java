package codes.laivy.dataapi.events.variables;

import codes.laivy.dataapi.modules.Variable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VariableEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Variable variable;

    public VariableEvent(boolean isAsync, Variable variable) {
        super(isAsync);
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

}
