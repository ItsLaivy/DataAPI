package net.redewhite.lvdataapi.variables.loaders;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import net.redewhite.lvdataapi.utils.VariableCreationController;

import static net.redewhite.lvdataapi.LvDataAPI.*;

public class InactiveTextLoader {

    private final TextVariableReceptor owner;
    private final Object value;
    private final String name;

    public InactiveTextLoader(String varname, Object value, TextVariableReceptor textVariable) {
        if (varname == null) throw new NullPointerException("Variable name cannot be null!");
        else if (textVariable == null) throw new NullPointerException("Variable owner cannot be null!");

        this.name = varname;
        this.value = value;
        this.owner = textVariable;

        for (VariableCreationController var : getVariables().keySet()) {
            if (var.getVariableName().equals(name)) {
                new TextVariableLoader(textVariable, var, value);
                return;
            }
        }
        getInactiveTextVariables().put(this, name);
    }

    public TextVariableReceptor getOwner() {
        return owner;
    }
    public Object getValue() {
        return value;
    }
    public String getName() {
        return name;
    }

}