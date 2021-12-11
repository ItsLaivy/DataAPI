package net.redewhite.lvdataapi.receptors;

import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.modules.VariableReceptorModule;
import net.redewhite.lvdataapi.modules.VariableReturnModule;

import java.util.ArrayList;
import java.util.List;

import static net.redewhite.lvdataapi.modules.VariableReturnModule.getVariableUnhashedValue;
import static net.redewhite.lvdataapi.modules.VariableReturnModule.getVariableHashedValue;
import static net.redewhite.lvdataapi.DataAPI.*;

public class ActiveVariableLoader {

    private final VariableReceptorModule receptor;
    private final VariableCreationModule variable;
    private Object value;

    public ActiveVariableLoader(VariableCreationModule variable, VariableReceptorModule receptor, Object value) {
        this.variable = variable;
        this.receptor = receptor;
        this.value = value;

        if (variable == null) throw new NullPointerException("active variable's variable name cannot be null");
        if (receptor == null) throw new NullPointerException("active variable's receptor cannot be null");

        if (!variable.isSuccessfullyCreated()) {
            throw new IllegalStateException("this variable was not created correctly.");
        }

        //noinspection Java8CollectionRemoveIf
        for (InactiveVariableLoader iv : new ArrayList<>(getInactiveVariables())) {
            if (iv.getTable() == variable.getTable() && iv.getVariableBruteID().equals(variable.getBruteID())) {
                getInactiveVariables().remove(iv);
            }
        }

        receptor.getVariables().add(this);
        getActiveVariables().add(this);
    }

    @SuppressWarnings("unused")
    public VariableReceptorModule getReceptor() {
        return receptor;
    }

    public VariableCreationModule getVariable() {
        return variable;
    }

    public Object getValue() {
        return getVariableUnhashedValue(value);
    }
    public VariableReturnModule getValueModule() {
        return new VariableReturnModule(getVariableUnhashedValue(value));
    }
    public void setValue(Object value) {
        this.value = getVariableHashedValue(value);
    }
}
