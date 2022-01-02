package net.redewhite.lvdataapi.receptors;

import net.redewhite.lvdataapi.DataAPI;
import net.redewhite.lvdataapi.developers.API;
import net.redewhite.lvdataapi.modules.VariableCreator;
import net.redewhite.lvdataapi.modules.TableCreator;

public class InactiveVariable {

    private final TableCreator table;
    private final String variableBruteID;
    private final String ownerBruteID;
    private final Object value;

    public InactiveVariable(String ownerBruteID, String variableBruteID, TableCreator table, Object value) {
        this.variableBruteID = variableBruteID;
        this.ownerBruteID = ownerBruteID;
        this.table = table;
        this.value = value;

        if (ownerBruteID == null) throw new NullPointerException("inactive variable's owner brute id cannot be null");
        if (variableBruteID == null) throw new NullPointerException("inactive variable's variable brute id cannot be null");
        if (table == null) throw new NullPointerException("inactive variable's table cannot be null");

        for (VariableCreator var : DataAPI.getVariables()) {
            if (var.getTable() == table && var.getBruteID().equals(variableBruteID)) {
                new ActiveVariable(var, API.getVariableReceptorByBruteID(ownerBruteID, table), value);
                return;
            }
        }

        DataAPI.getInactiveVariables().add(this);
    }

    public String getOwnerBruteID() {
        return ownerBruteID;
    }
    public String getVariableBruteID() {
        return variableBruteID;
    }
    public Object getValue() {
        return DataAPI.getVariableUnhashedValue(value);
    }
    public TableCreator getTable() {
        return table;
    }
}
