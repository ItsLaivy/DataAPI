package net.redewhite.lvdataapi.developers.events.variables;

import net.redewhite.lvdataapi.modules.VariableCreator;

public class VariableCreateEvent extends VariableLoadEvent {
    public VariableCreateEvent(boolean isAsync, VariableCreator variable) {
        super(isAsync, variable);
    }
}
