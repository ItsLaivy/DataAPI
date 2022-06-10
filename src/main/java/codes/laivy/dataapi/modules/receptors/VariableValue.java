package codes.laivy.dataapi.modules.receptors;

import codes.laivy.dataapi.modules.Creator;
import codes.laivy.dataapi.modules.Receptor;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import java.io.Serializable;
import java.util.Map;

import static codes.laivy.dataapi.main.DataAPI.plugin;

public class VariableValue<T extends Serializable> {

    private final Receptor receptor;
    private final ActiveVariable variable;

    public VariableValue(@NotNull Receptor receptor, @NotNull Plugin plugin, @NotNull String name) {
        Validate.notNull(receptor, "variable value's receptor cannot be null");
        Validate.notNull(plugin, "variable value's variable plugin cannot be null");
        Validate.notNull(name, "variable value's variable name cannot be null");

        if (!receptor.isSuccessfullyCreated()) {
            throw new IllegalStateException("this receptor was not created correctly.");
        }

        Map<String, ActiveVariable> map = plugin().getActiveVariables().get(receptor.getTable()).get(receptor.getBruteId());
        String bruteId = Creator.getBruteIdOf(Creator.CreatorType.VARIABLE_CREATOR, plugin, name, null);

        if (map.containsKey(bruteId)) {
            this.receptor = receptor;
            this.variable = map.get(bruteId);
        } else {
            throw new NullPointerException("Cannot find any variable with that parameters (Name: " + name + ", Plugin: " + plugin.getName() + ", Receptor (BruteID): " + receptor.getBruteId() + ")");
        }
    }

    public VariableValue(@NotNull Receptor receptor, @NotNull ActiveVariable activeVariable) {
        this(receptor, activeVariable.getVariable().getPlugin(), activeVariable.getVariable().getName());
    }
    public VariableValue(@NotNull Receptor receptor, @NotNull String name) {
        this(receptor, plugin(), name);
    }

    private void check(@NotNull Object object) {
        Validate.notNull(object, "null value");
    }

    @Nullable
    public T getValue() {
        //noinspection unchecked
        return (T) this.variable.getValue();
    }
    public void setValue(@Nullable T value) {
        this.variable.setValue(value);
    }

    public void addValue(@NotNull Byte value) {
        check(value);

        if (getValue() instanceof Byte) {
            variable.setValue((Byte) getValue() + value);
        }
    }
    public void addValue(@NotNull Double value) {
        check(value);

        if (getValue() instanceof Double) {
            variable.setValue((Double) getValue() + value);
        }
    }
    public void addValue(@NotNull Float value) {
        check(value);

        if (getValue() instanceof Float) {
            variable.setValue((Float) getValue() + value);
        }
    }
    public void addValue(@NotNull Integer value) {
        check(value);

        if (getValue() instanceof Integer) {
            variable.setValue((Integer) getValue() + value);
        }
    }
    public void addValue(@NotNull Long value) {
        check(value);

        if (getValue() instanceof Long) {
            variable.setValue((Long) getValue() + value);
        }
    }
    public void addValue(@NotNull Short value) {
        check(value);

        if (getValue() instanceof Short) {
            variable.setValue((Short) getValue() + value);
        }
    }

    @NotNull
    public Receptor getReceptor() {
        return receptor;
    }

    @NotNull
    public ActiveVariable getVariable() {
        return variable;
    }

}
