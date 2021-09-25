package net.redewhite.lvdataapi.loaders;

import net.redewhite.lvdataapi.events.api.variables.InactiveVariableCreateEvent;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.creators.VariablesTable;
import net.redewhite.lvdataapi.developers.AdvancedAPI;
import net.redewhite.lvdataapi.developers.EasyAPI;
import net.redewhite.lvdataapi.DataAPI;

import static net.redewhite.lvdataapi.DataAPI.*;

public class InactiveVariableLoader {

    private final Object value;
    private final String ownerBruteId;
    private final VariablesTable table;
    private final String variableBruteId;

    public InactiveVariableLoader(VariablesTable table, String variableBruteId, Object value, String owner_brute_id) {
        if (variableBruteId == null) throw new NullPointerException("Variable name brute id cannot be null!");
        else if (owner_brute_id == null) throw new NullPointerException("Variable owner brute id cannot be null!");

        this.variableBruteId = variableBruteId;
        this.ownerBruteId = owner_brute_id;
        this.value = AdvancedAPI.getVariableUnhashedValue(value);
        this.table = table;

        for (VariableCreationModule var : getVariables().keySet()) {
            if (var.getVariableBruteId().equals(variableBruteId)) {
                new ActiveVariableLoader(EasyAPI.getVariableReceptor(table.getPlugin(), owner_brute_id, table), this.value, var);
                return;
            }
        }

        InactiveVariableCreateEvent event = new InactiveVariableCreateEvent(this, EasyAPI.getVariableReceptor(table.getPlugin(), owner_brute_id, table));
        instance.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            DataAPI.getInactiveVariables().put(this, variableBruteId);
        }

    }

    public String getOwnerBruteId() {
        return ownerBruteId;
    }
    public Object getValue() {
        return AdvancedAPI.getVariableUnhashedValue(value);
    }
    public VariablesTable getTable() {
        return table;
    }
    public String getVariableBruteId() {
        return variableBruteId;
    }

}
