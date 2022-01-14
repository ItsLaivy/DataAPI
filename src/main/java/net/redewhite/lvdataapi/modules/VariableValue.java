package net.redewhite.lvdataapi.modules;

import net.redewhite.lvdataapi.developers.events.variables.ActiveVariableValueChangeEvent;
import net.redewhite.lvdataapi.receptors.ActiveVariable;
import net.redewhite.lvdataapi.types.VariablesType;
import net.redewhite.lvdataapi.types.variables.Pair;
import org.bukkit.Bukkit;

import java.util.*;

import static net.redewhite.lvdataapi.DataAPI.getVariableHashedValue;
import static net.redewhite.lvdataapi.types.VariablesType.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VariableValue {

    private final ActiveVariable variable;

    private final VariablesType t;

    public VariableValue(ActiveVariable variable) {
        this.variable = variable;
        this.t = variable.getVariable().getType();
    }

    public void removeMapValue(String key) {
        if (t == MAP) {
            Map<String, String> map = asMap();
            map.remove(key);

            variable.setValue(replaceMapVariable(map));
        } else {
            throw new IllegalStateException("The variable needs to be a MapVariable");
        }
    }
    public void addValue(String key, String value) {
        if (t == MAP) {
            Map<String, String> map = asMap();
            map.put(getVariableHashedValue(key), getVariableHashedValue(value));

            variable.setValue(replaceMapVariable(map));
        } else {
            throw new IllegalStateException("The variable needs to be a MapVariable");
        }
    }
    public void setValue(String key, String value) {
        if (t == MAP || t == PAIR) {
            if (t == MAP) {
                Map<String, String> map = new HashMap<>();
                map.put(getVariableHashedValue(key), getVariableHashedValue(value));

                variable.setValue(replaceMapVariable(map));
            } else {
                Pair<String, String> pair = new Pair<>(getVariableHashedValue(key), getVariableHashedValue(value));
                variable.setValue(replacePairVariable(pair));
            }
        } else {
            throw new IllegalStateException("The variable needs to be a MapVariable or a PairVariable");
        }
    }

    public void setValue(Object value) {
        ActiveVariableValueChangeEvent event = new ActiveVariableValueChangeEvent(this.variable, value);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (value instanceof Map || value instanceof Pair) {
            throw new IllegalStateException("Use setValue(key, value) to change map/pair variable's values");
        }

        String newValue = null;

        if (value != null) {
            if (value instanceof List) {
                ArrayList<Object> array = new ArrayList<>();
                for (Object e : (ArrayList<?>) value) {
                    array.add(getVariableHashedValue(e));
                }

                if (array.size() != 0) {
                    newValue = replaceListVariable(array);
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

        if (t == MAP) {
            throw new IllegalStateException("Use removeMapValue() to remove map variable's values");
        }

        if (t == PAIR) {
            throw new IllegalStateException("You cannot remove values from a Pair variable!");
        }

        if (t == LIST) {
            List<Object> newList = new ArrayList<>();
            List<Object> addToNewList = new ArrayList<>();
            if (value instanceof List) {
                addToNewList.addAll((ArrayList<?>) value);
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

            String e = replaceListVariable(newList);
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

        if (value instanceof Map) {
            throw new IllegalStateException("Use addValue(key, value) to add map variable's values");
        }

        if (t == PAIR) {
            throw new IllegalStateException("You cannot add values into a Pair variable!");
        }

        if (t == LIST) {
            List<Object> newList = asList();
            if (value instanceof List) {
                newList.addAll((ArrayList<?>) value);
            } else {
                newList.add(value);
            }

            String e = replaceListVariable(newList);
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

    public Pair<String, String> asPair() {
        if (value() == null) return new Pair<>();

        String[] split = value().toString().split("<PAIRSPLIT!>");
        return new Pair<>(split[0], split[1]);
    }
    public Map<String, String> asMap() {
        if (value() == null) return new HashMap<>();

        Map<String, String> map = new HashMap<>();
        for (String e : value().toString().split("<SPLIT!>")) {
            String[] split = e.split("<MAPSPLIT!>");
            map.put(split[0], split[1]);
        }

        return map;
    }
    public List<Object> asList() {
        if (value() == null) return new ArrayList<>();

        String[] split = value().toString().split("<SPLIT!>");
        return new ArrayList<>(Arrays.asList(split));
    }

    public boolean isNull() {
        return value() == null;
    }

    public Integer asInt() {
        if (value() == null) return null;
        return Integer.parseInt(value().toString());
    }

    public Long asLong() {
        if (value() == null) return null;

        try {
            return Long.parseLong(value().toString());
        } catch (NumberFormatException ignore) {
            return ((long) Double.parseDouble(value().toString()));
        }
    }

    public Double asDouble() {
        if (value() == null) return null;
        return Double.parseDouble(value().toString());
    }

    public Object asList(int index) {
        return asList().get(index);
    }

    public Byte asByte() {
        if (value() == null) return null;
        return Byte.parseByte(value().toString());
    }

    public Boolean asBoolean() {
        if (value() == null) return null;
        return Boolean.parseBoolean(value().toString());
    }

    public UUID asUUID() {
        if (value() == null) return null;
        return UUID.fromString(value().toString());
    }

    public Object get() {
        return value();
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
