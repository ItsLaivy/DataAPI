package net.redewhite.lvdataapi.loaders;

import net.redewhite.lvdataapi.events.api.variables.ActiveVariableCreateEvent;
import net.redewhite.lvdataapi.modules.VariableCreationModule;
import net.redewhite.lvdataapi.receptors.VariableReceptor;
import net.redewhite.lvdataapi.developers.AdvancedAPI;

import java.util.ArrayList;

import static net.redewhite.lvdataapi.DataAPI.*;

public class ActiveVariableLoader {

    private final VariableCreationModule variable;
    private final String owner_brute_id;
    private final String owner_name;
    private Object value;

    public ActiveVariableLoader(VariableReceptor receptor, Object value, VariableCreationModule variable) {
        if (variable == null) throw new NullPointerException("Variable cannot be null!");
        else if (receptor == null) throw new NullPointerException("Variable receptor cannot be null!");

        this.owner_brute_id = receptor.getNameBruteId();
        this.owner_name = receptor.getName();
        this.variable = variable;
        this.value = AdvancedAPI.getVariableUnhashedValue(value);

        ActiveVariableCreateEvent event = new ActiveVariableCreateEvent(this, receptor);
        instance.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            process();
        }
    }

    private void process() {

        for (ActiveVariableLoader var : getActiveVariables().keySet()) {
            if (var.getVariable().getVariableBruteId().equals(this.getVariable().getVariableBruteId())) {
                if (var.getOwnerBruteId().equals(owner_brute_id)) {
                    getMessage("Normal variable already defined", variable.getVariableBruteId(), getOwnerName());
                    return;
                }
            }
        }

        if (variable.getDataType() == variableDataType.NORMAL || variable.getDataType() == variableDataType.ARRAY) {
            ArrayList<InactiveVariableLoader> array = new ArrayList<>();
            for (InactiveVariableLoader var : getInactiveVariables().keySet()) {
                if (var.getOwnerBruteId().equals(owner_brute_id)) {
                    if (var.getVariableBruteId().equals(variable.getVariableBruteId())) {
                        this.value = var.getValue();
                        array.add(var);
                    }
                }
            } for (InactiveVariableLoader e : array) getInactiveVariables().remove(e);
        }

        getActiveVariables().put(this, owner_brute_id);
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public VariableCreationModule getVariable() {
        return variable;
    }
    public String getOwnerBruteId() {
        return owner_brute_id;
    }
    public String getOwnerName() {
        return owner_name;
    }
}
