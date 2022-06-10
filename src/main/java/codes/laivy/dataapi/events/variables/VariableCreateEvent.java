package codes.laivy.dataapi.events.variables;

import codes.laivy.dataapi.modules.Variable;

public class VariableCreateEvent extends VariableLoadEvent {
    public VariableCreateEvent(boolean isAsync, Variable variable) {
        super(isAsync, variable);
    }
}
