package codes.laivy.dataapi.events.variables;

import codes.laivy.dataapi.modules.Variable;

public class VariableLoadEvent extends VariableEvent {
    public VariableLoadEvent(boolean isAsync, Variable variable) {
        super(isAsync, variable);
    }
}
