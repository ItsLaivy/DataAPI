package net.redewhite.lvdataapi.receptors;

import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.modules.TableCreationModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static net.redewhite.lvdataapi.modules.VariableReturnModule.getVariableUnhashedValue;
import static net.redewhite.lvdataapi.developers.API.getVariableReceptorByBruteID;
import static net.redewhite.lvdataapi.DataAPI.getInactiveVariables;
import static net.redewhite.lvdataapi.DataAPI.getVariables;

public class InactiveVariableLoader {

    private final TableCreationModule table;
    private final String variableBruteID;
    private final String ownerBruteID;
    private final Object value;

    public InactiveVariableLoader(String ownerBruteID, String variableBruteID, TableCreationModule table, Object value) {
        this.variableBruteID = variableBruteID;
        this.ownerBruteID = ownerBruteID;
        this.table = table;
        this.value = value;

        if (ownerBruteID == null) throw new NullPointerException("inactive variable's owner brute id cannot be null");
        if (variableBruteID == null) throw new NullPointerException("inactive variable's variable brute id cannot be null");
        if (table == null) throw new NullPointerException("inactive variable's table cannot be null");

        for (VariableCreationModule var : getVariables()) {
            if (var.getTable() == table && var.getBruteID().equals(variableBruteID)) {
                new ActiveVariableLoader(var, getVariableReceptorByBruteID(ownerBruteID, table), value);
                return;
            }
        }

        getInactiveVariables().add(this);
    }

    public String getOwnerBruteID() {
        return ownerBruteID;
    }
    public String getVariableBruteID() {
        return variableBruteID;
    }
    public Object getValue() {
        return getVariableUnhashedValue(value);
    }
    public TableCreationModule getTable() {
        return table;
    }
}
