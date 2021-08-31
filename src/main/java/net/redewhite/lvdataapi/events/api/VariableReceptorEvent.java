package net.redewhite.lvdataapi.events.api;

import net.redewhite.lvdataapi.creators.VariablesTable;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import org.bukkit.event.Event;

public abstract class VariableReceptorEvent extends Event {

    private final VariableReceptor receptor;
    private final VariablesTable table;

    public VariableReceptorEvent(VariableReceptor receptor) {
        this.receptor = receptor;
        this.table = receptor.getTable();
    }
    public VariableReceptorEvent(VariableReceptor receptor, VariablesTable table) {
        this.receptor = receptor;
        this.table = table;
    }

    public VariablesTable getTable() {
        return table;
    }
    public final VariableReceptor getReceptor() {
        return receptor;
    }

}
