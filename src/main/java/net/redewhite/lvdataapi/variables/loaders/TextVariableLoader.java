package net.redewhite.lvdataapi.variables.loaders;

import net.redewhite.lvdataapi.variables.receptors.TextVariableReceptor;
import net.redewhite.lvdataapi.utils.VariableCreationController;
import org.bukkit.Bukkit;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.LvDataAPI.variableType.NORMAL;
import static net.redewhite.lvdataapi.LvDataAPI.variableType.ARRAY;
import static net.redewhite.lvdataapi.LvDataAPI.*;

public class TextVariableLoader {

    private final VariableCreationController variable;
    private final TextVariableReceptor ownertextvar;
    private Object value;

    public TextVariableLoader(TextVariableReceptor textVariable, VariableCreationController variable, Object value) {
        if (variable == null) throw new NullPointerException("Variable cannot be null!");
        else if (textVariable == null) throw new NullPointerException("Text variable cannot be null!");

        this.variable = variable;
        this.ownertextvar = textVariable;
        this.value = value;

        if (variable.getType() == NORMAL || variable.getType() == ARRAY) {
            ArrayList<InactiveTextLoader> array = new ArrayList<>();
            for (InactiveTextLoader var : getInactiveTextVariables().keySet()) {
                if (var.getOwner() == ownertextvar) {
                    if (var.getName().equals(variable.getVariableName())) {
                        this.value = var.getValue();
                        array.add(var);
                    }
                }
            } for (InactiveTextLoader e : array) getInactiveTextVariables().remove(e);
        }

        for (TextVariableLoader var : getTextVariables().keySet()) {
            if (var.getVariable().getName().equals(variable.getName())) {
                if (var.getVariable().getPlugin() == variable.getPlugin()) {
                    if (var.getTextVariable() == textVariable) {
                        return;
                    }
                }
            }
        }

        getTextVariables().put(this, ownertextvar);
    }

    public void setValue(Object value) {
        this.value = value;
    }
    public VariableCreationController getVariable() {
        return variable;
    }
    public TextVariableReceptor getTextVariable() {
        return ownertextvar;
    }
    public String getName() {
        return ownertextvar.getName();
    }
    public Object getValue() {
        return value;
    }
}