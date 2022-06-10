package codes.laivy.dataapi.modules.receptors;

import codes.laivy.dataapi.modules.Receptor;
import codes.laivy.dataapi.modules.Variable;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class InactiveVariable {

    private final Receptor receptor;
    private final String variable;
    private final String value;

    public InactiveVariable(@NotNull Receptor receptor, @NotNull String variableBruteID, @NotNull String value) {
        Validate.notNull(receptor, "inactive variable's receptor cannot be null");
        Validate.notNull(variableBruteID, "inactive variable's variable brute id cannot be null");
        Validate.notNull(value, "inactive variable's value cannot be null");

        this.variable = variableBruteID;
        this.receptor = receptor;
        this.value = value;

        if (!receptor.isSuccessfullyCreated()) {
            throw new IllegalStateException("this receptor was not created correctly");
        }

        if (plugin().getVariables().get(receptor.getTable()).containsKey(variableBruteID)) {
            Variable var = plugin().getVariables().get(receptor.getTable()).get(variableBruteID);
            new ActiveVariable(var, receptor, var.getVariableUnHashedValue(Variable.stringToByteArray(value)));
        }

        plugin().getInactiveVariables().get(receptor.getTable()).get(receptor.getBruteId()).put(variableBruteID, this);
    }

    @NotNull
    public Receptor getReceptor() {
        return receptor;
    }

    @NotNull
    public String getVariable() {
        return variable;
    }

    @NotNull
    public String getValue() {
        return value;
    }

}
