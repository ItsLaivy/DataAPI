package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.variables.ActiveVariableValueChangeEvent;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.VariablesType;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.redewhite.lvdataapi.DataAPI.getVariableHashedValue;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VariableValue {

    private final ActiveVariable variable;

    public VariableValue(ActiveVariable variable) {
        this.variable = variable;
    }

    public void setValue(Object value) {
        ActiveVariableValueChangeEvent event = new ActiveVariableValueChangeEvent(this.variable, value);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        String newValue = null;

        if (value != null) {
            if (value instanceof ArrayList<?>) {
                ArrayList<Object> array = new ArrayList<>();
                for (Object e : new ArrayList<>(Arrays.asList(((ArrayList<?>) value).toArray()))) {
                    array.add(getVariableHashedValue(e));
                }

                if (array.size() != 0) {
                    newValue = array.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
                }
            } else {
                newValue = getVariableHashedValue(value);
            }
        }

        if (getVariableHashedValue(variable.getValue()).equals(newValue)) return;
        variable.setValue(newValue);
    }
    public void removeValue(Object value) {
        if (value == null) return;

        if (variable.getValue() == null) {
            variable.setValue(value);
            return;
        }

        if (variable.getVariable().getType() == VariablesType.ARRAY) {
            List<Object> newList = new ArrayList<>();
            List<Object> addToNewList = new ArrayList<>();
            if (value instanceof ArrayList<?>) {
                addToNewList.addAll(Arrays.asList(((ArrayList<?>) value).toArray()));
            } else {
                addToNewList.add(value);
            }

            for (Object a : addToNewList) {
                boolean success = false;
                for (Object e : variable.getValueModule().asList()) {
                    if (!success) {
                        if (e.equals(a)) {
                            success = true;
                            continue;
                        }
                    }
                    newList.add(e);
                }
            }

            String e = newList.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            if (newList.size() == 0) variable.setValue(null);
            else variable.setValue(e);
        } else {
            try {
                Double.parseDouble(value.toString());
            } catch (NumberFormatException ignore) {
                try {
                    Double.parseDouble(variable.getValue().toString());
                } catch (NumberFormatException ignore2) {
                    throw new IllegalStateException("the variable value isn't a number");
                }
                throw new IllegalStateException("the value isn't a number");
            } try {
                Double.parseDouble(variable.getValue().toString());
            } catch (NumberFormatException ignore2) {
                throw new IllegalStateException("the variable value isn't a number");
            }

            double num1 = Double.parseDouble(variable.getValue().toString());
            double num2 = Double.parseDouble(value.toString());

            variable.setValue(num1 - num2);
        }
    }
    public void addValue(Object value) {
        if (value == null) return;

        if (variable.getValue() == null) {
            variable.setValue(value);
            return;
        }

        if (variable.getVariable().getType() == VariablesType.ARRAY) {
            List<Object> newList = variable.getValueModule().asList();
            if (value instanceof ArrayList<?>) {
                newList.addAll(Arrays.asList(((ArrayList<?>) value).toArray()));
            } else {
                newList.add(value);
            }

            String e = newList.toString().replace(", ", "<SPLIT!>").replace("[", "").replace("]", "");
            variable.setValue(e);
        } else {
            if (value instanceof Number && variable.getValue() instanceof Number) {
                throw new IllegalStateException("Esse valor ou o valor da variável não é um número");
            }

            double num1 = Float.parseFloat(variable.getValue().toString());
            double num2 = Float.parseFloat(value.toString());

            variable.setValue(num1 + num2);
        }
    }

    public String asString() {
        if (value() == null) return null;

        return value().toString();
    }

    public boolean isNull() {
        return value() == null;
    }

    public Integer asInt() {
        if (value() == null) return null;

        try {
            return Integer.parseInt(value().toString());
        } catch (NumberFormatException ignore) {
            try {
                return ((int) Double.parseDouble(value().toString()));
            } catch (NumberFormatException ignore2) {
                throw new NumberFormatException("thats value cannot be cast to integer.");
            }
        }
    }

    public Long asLong() {
        if (value() == null) return null;

        try {
            return Long.parseLong(value().toString());
        } catch (NumberFormatException ignore) {
            try {
                return ((long) Double.parseDouble(value().toString()));
            } catch (NumberFormatException ignore2) {
                throw new NumberFormatException("thats value cannot be cast to long.");
            }
        }
    }

    public Double asDouble() {
        if (value() == null) return null;

        try {
            return Double.parseDouble(value().toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("thats value cannot be cast to double.");
        }
    }

    public List<Object> asList() {
        if (value() == null) return new ArrayList<>();

        String[] split = value().toString().split("<SPLIT!>");
        return new ArrayList<>(Arrays.asList(split));
    }

    public Object asList(int i) {
        if (value() == null) return new ArrayList<>();

        String[] split = value().toString().split("<SPLIT!>");
        return Arrays.asList(split).get(i);
    }

    public Byte asByte() {
        if (value() == null) return null;

        try {
            return Byte.parseByte(value().toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("thats value cannot be cast to byte.");
        }
    }

    public Boolean asBoolean() {
        if (value() == null) return null;

        try {
            return Boolean.parseBoolean(value().toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("thats value cannot be cast to boolean.");
        }
    }

    @Override
    public String toString() {
        if (value() == null) return null;

        return value().toString();
    }

    Object value() {
        return variable.getValue();
    }
}
