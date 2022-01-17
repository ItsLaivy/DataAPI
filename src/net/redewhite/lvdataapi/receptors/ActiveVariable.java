package net.redewhite.lvdataapi.receptors;

import net.redewhite.lvdataapi.modules.VariableCreator;
import net.redewhite.lvdataapi.modules.ReceptorCreator;
import net.redewhite.lvdataapi.modules.VariableValue;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.DataAPI.*;

public class ActiveVariable {

    private final ReceptorCreator receptor;
    private final VariableCreator variable;
    private Object value;

    public ActiveVariable(VariableCreator variable, ReceptorCreator receptor, Object value) {
        this.variable = variable;
        this.receptor = receptor;
        this.value = value;

        if (variable == null) throw new NullPointerException("active variable's variable name cannot be null");
        if (receptor == null) throw new NullPointerException("active variable's receptor cannot be null");

        if (!variable.isSuccessfullyCreated()) {
            throw new IllegalStateException("this variable was not created correctly.");
        }

        //noinspection Java8CollectionRemoveIf
        for (InactiveVariable iv : new ArrayList<>(getInactiveVariables())) {
            if (iv.getTable() == variable.getTable() && iv.getVariableBruteID().equals(variable.getBruteID())) {
                getInactiveVariables().remove(iv);
            }
        }

        receptor.getVariables().add(this);
        getActiveVariables().add(this);
    }

    @SuppressWarnings("unused")
    public ReceptorCreator getReceptor() {
        return receptor;
    }

    public VariableCreator getVariable() {
        return variable;
    }

    public Object getValue() {
        return getVariableUnhashedValue(value);
    }
    public VariableValue getValueModule() {
        return new VariableValue(this);
    }
    public void setValue(Object value) {
        this.value = getVariableHashedValue(value);
    }
}
