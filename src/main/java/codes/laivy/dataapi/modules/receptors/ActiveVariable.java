package codes.laivy.dataapi.modules.receptors;

import codes.laivy.dataapi.events.variables.receptors.ActiveVariableValueChangeEvent;
import codes.laivy.dataapi.modules.Receptor;
import codes.laivy.dataapi.modules.Variable;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.io.Serializable;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class ActiveVariable {

    private final Receptor receptor;
    private final Variable variable;
    private Serializable value;

    public ActiveVariable(@NotNull Variable variable, @NotNull Receptor receptor, @NotNull Serializable value) {
        Validate.notNull(receptor, "active variable's receptor cannot be null");
        Validate.notNull(variable, "active variable's variable cannot be null");

        this.variable = variable;
        this.receptor = receptor;
        this.value = value;

        if (!variable.isSuccessfullyCreated()) {
            throw new IllegalStateException("this variable was not created correctly.");
        }
        if (!receptor.isSuccessfullyCreated()) {
            throw new IllegalStateException("this receptor was not created correctly.");
        }

        if (plugin().getInactiveVariables().get(receptor.getTable()).get(receptor.getBruteId()).containsKey(variable.getBruteId())) {
            InactiveVariable inactiveVariable = plugin().getInactiveVariables().get(receptor.getTable()).get(receptor.getBruteId()).get(variable.getBruteId());

            this.value = variable.getVariableUnHashedValue(Variable.stringToByteArray(inactiveVariable.getValue()));
            plugin().getInactiveVariables().get(receptor.getTable()).get(receptor.getBruteId()).remove(variable.getBruteId());
        }

        plugin().getActiveVariables().get(receptor.getTable()).get(receptor.getBruteId()).put(variable.getBruteId(), this);
    }

    @NotNull
    public Receptor getReceptor() {
        return receptor;
    }

    @NotNull
    public Variable getVariable() {
        return variable;
    }

    @NotNull
    public Serializable getValue() {
        return this.value;
    }

    public void setValue(@NotNull Serializable value) {
        Validate.isTrue(getReceptor().isLoaded(), "Cannot set the variable value because receptor hasn't loaded");

        ActiveVariableValueChangeEvent event = new ActiveVariableValueChangeEvent(!Bukkit.isPrimaryThread(), this, value);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        this.value = value;
    }

}
