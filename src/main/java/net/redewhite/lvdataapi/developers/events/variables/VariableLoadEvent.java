package net.redewhite.lvdataapi.developers.events.variables;

import net.redewhite.lvdataapi.modules.VariableCreator;

public class VariableLoadEvent extends VariableEvent {
    public VariableLoadEvent(boolean isAsync, VariableCreator variable) {
        super(isAsync, variable);
    }
}
