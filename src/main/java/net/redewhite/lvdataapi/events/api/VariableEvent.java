package net.redewhite.lvdataapi.events.api;

import net.redewhite.lvdataapi.loaders.ActiveVariableLoader;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import org.bukkit.event.Event;

public abstract class VariableEvent extends Event {

    private final ActiveVariableLoader variable;
    private final VariableReceptor receptor;

    public VariableEvent(ActiveVariableLoader variable, VariableReceptor receptor) {
        this.variable = variable;
        this.receptor = receptor;
    }

    public final ActiveVariableLoader getVariable() {
        return variable;
    }

    public final VariableReceptor getReceptor() {
        return receptor;
    }
}
